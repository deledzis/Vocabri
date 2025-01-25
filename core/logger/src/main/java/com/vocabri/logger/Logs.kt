package com.vocabri.logger

import co.touchlab.kermit.Logger

// Global function to create a logger with the class name as the tag
inline fun <reified T> T.logger(): Logger = Logger.withTag(T::class.java.simpleName)

fun logger(tag: String): Logger = Logger.withTag(tag)
