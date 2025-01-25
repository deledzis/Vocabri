package com.vocabri.utils

interface IdGenerator {
    fun generateStringId(): String

    fun generateLongId(): Long = generateStringId().toLong()
}
