/*
 * MIT License
 *
 * Copyright (c) 2025 Aleksandr Stiagov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
