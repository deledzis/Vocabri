package com.vocabri

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

fun Project.applySpotless() {
    plugins.apply(SpotlessPlugin::class)
    configureSpotless()
}

private fun Project.configureSpotless() {
    configure<SpotlessExtension> {
        format("misc") {
            target("**/*.gradle", "**/*.md", "**/.gitignore")

            indentWithSpaces()
            trimTrailingWhitespace()
            endWithNewline()
        }

        kotlin {
            target("**/*.kt", "**/*.kts")
            targetExclude("**/build/**", "**/tmp/**", "**/resources/**", "**/.gradle/**")

            val ktlintVersion = libs.findVersion("ktlint").get().toString()
            ktlint(ktlintVersion)
                .setEditorConfigPath("$projectDir/.editorconfig")
                .editorConfigOverride(
                    mapOf(
                        "android" to "true",
                        "max_line_length" to "120",
                    ),
                )
        }

        format("xml") {
            target("**/*.xml")
            targetExclude("**/build/**", "**/.gradle/**")
        }

        json {
            target("**/*.json")
            targetExclude("**/build/**", "**/.gradle/**")
        }

        yaml {
            target("**/*.yml", "**/*.yaml")
            targetExclude("**/build/**", "**/.gradle/**")
        }
    }
}
