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
package com.vocabri.data.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class TimeBasedIdGeneratorTest {

    private lateinit var generator: TimeBasedIdGenerator

    @Before
    fun setUp() {
        generator = TimeBasedIdGenerator()
    }

    @Test
    fun testGenerateStringIdNotEmpty() {
        val id = generator.generateStringId()
        assertTrue("Generated ID should not be empty", id.isNotEmpty())
    }

    @Test
    fun testGenerateStringIdIsNumeric() {
        val id = generator.generateStringId()
        try {
            id.toLong()
        } catch (e: NumberFormatException) {
            fail("Generated ID should be numeric. Received: $id")
        }
    }

    @Test
    fun testGenerateStringIdUniqueness() {
        val generatedIds = mutableSetOf<String>()
        val iterations = 1000

        repeat(iterations) {
            val id = generator.generateStringId()
            assertFalse("Duplicate ID detected: $id", generatedIds.contains(id))
            generatedIds.add(id)
        }
        assertEquals("All generated IDs should be unique", iterations, generatedIds.size)
    }
}
