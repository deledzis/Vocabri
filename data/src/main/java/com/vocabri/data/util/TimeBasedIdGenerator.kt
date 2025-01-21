package com.vocabri.data.util

import com.vocabri.domain.util.IdGenerator

/**
 * ID generator that creates unique string IDs based on the current system time in milliseconds.
 *
 * This implementation is simple and ensures IDs are unique as long as they are generated
 * in distinct milliseconds.
 */
class TimeBasedIdGenerator : IdGenerator {
    /**
     * Generates a unique string ID based on the current system time in milliseconds.
     *
     * @return A string representation of the current system time in milliseconds.
     */
    override fun generateStringId(): String = System.currentTimeMillis().toString()
}
