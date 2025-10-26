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
package com.vocabri.notifications

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PushNotificationServiceLocatorTest {

    @After
    fun tearDown() {
        PushNotificationServiceLocator.unregister()
    }

    @Test
    fun `register exposes repository`() {
        val repository = FakeRepository()

        PushNotificationServiceLocator.register(repository)

        assertEquals(repository, PushNotificationServiceLocator.tokenRepository())
    }

    @Test
    fun `unregister clears repository`() {
        val repository = FakeRepository()
        PushNotificationServiceLocator.register(repository)

        PushNotificationServiceLocator.unregister()

        assertNull(PushNotificationServiceLocator.tokenRepository())
    }

    private class FakeRepository : PushNotificationTokenRepository {
        private val _token = MutableStateFlow<String?>(null)

        override val token: StateFlow<String?> = _token.asStateFlow()

        override fun update(token: String) {
            _token.value = token
        }

        override fun clear() {
            _token.value = null
        }
    }
}
