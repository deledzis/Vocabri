package com.vocabri

import org.gradle.api.plugins.PluginManager

fun PluginManager.applyCommonPlugins() {
    apply("org.jetbrains.kotlin.android")
}
