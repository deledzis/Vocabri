package com.vocabri.domain.util

interface IdGenerator {
    fun generateStringId(): String

    fun generateLongId(): Long = generateStringId().toLong()
}
