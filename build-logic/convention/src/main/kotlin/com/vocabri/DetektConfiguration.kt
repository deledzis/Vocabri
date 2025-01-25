package com.vocabri

import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.DetektPlugin
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

fun Project.applyDetekt() {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    subprojects {
        pluginManager.apply(DetektPlugin::class)

        configure<DetektExtension> {
            buildUponDefaultConfig = true
            autoCorrect = true
            parallel = true
            config.setFrom(
                files("$rootDir/.detekt/detekt-config.yml"),
            )
        }

        dependencies {
            add("detektPlugins", libs.findLibrary("detekt-compose-rules").get())
        }

        tasks.withType<Detekt>().configureEach {
            jvmTarget = JvmVersionConfig.jvmTarget.toString()
            reports {
                ignoreFailures = false
                xml.required.set(true)
                html.required.set(true)
                xml.outputLocation.set(layout.buildDirectory.file("reports/detekt/detekt.xml"))
                html.outputLocation.set(layout.buildDirectory.file("reports/detekt/detekt.html"))
            }
        }

        tasks.withType<DetektCreateBaselineTask>().configureEach {
            jvmTarget = JvmVersionConfig.jvmTarget.toString()
        }
    }
}
