package com.vocabri

import kotlinx.kover.gradle.plugin.KoverGradlePlugin
import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import kotlinx.kover.gradle.plugin.dsl.KoverReportFiltersConfig
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

fun Project.applyKover() {
    pluginManager.apply(KoverGradlePlugin::class)
}

fun Project.applyKoverExclusions() {
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

        annotatedBy(
            "*ExcludeFromCoverage",
            "*Composable",
            "androidx.compose.ui.tooling.preview.Preview",
        )
        packages(
            "com.vocabri.ui.theme",
            "com.vocabri.ui.theme.*",
            "com.vocabri.data.data",
            "com.vocabri.data.db",
        )
        classes(
            "*.BuildConfig",
            "*.R",
            "*.BR",
            "android.*",
            "*ComposableSingletons",
            "*ComposableSingletons*",
            "*ModuleKt",
        )
    }
}
