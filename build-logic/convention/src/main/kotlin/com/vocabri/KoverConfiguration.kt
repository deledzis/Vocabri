package com.vocabri

import kotlinx.kover.gradle.plugin.KoverGradlePlugin
import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import kotlinx.kover.gradle.plugin.dsl.KoverReportFiltersConfig
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

fun Project.applyKover() {
    pluginManager.apply(KoverGradlePlugin::class)
    extensions.configure<KoverProjectExtension>("kover") {
        reports {
            filters {
                coverageExclusions()
            }
        }
    }
}

private fun KoverReportFiltersConfig.coverageExclusions() {
    excludes {
        androidGeneratedClasses()
    }
}
