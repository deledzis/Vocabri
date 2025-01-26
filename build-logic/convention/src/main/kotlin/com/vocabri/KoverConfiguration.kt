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
