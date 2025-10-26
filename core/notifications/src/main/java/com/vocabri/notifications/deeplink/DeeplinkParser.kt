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
package com.vocabri.notifications.deeplink

import android.net.Uri
import com.vocabri.logger.logger

/**
 * Responsible for translating raw deeplink payloads into [DeeplinkData] instances.
 */
class DeeplinkParser {
    private val log = logger()

    fun parse(rawUri: String?): DeeplinkData? {
        if (rawUri.isNullOrBlank()) {
            log.v { "No deeplink provided" }
            return null
        }
        return runCatching { Uri.parse(rawUri.trim()) }
            .onFailure { throwable ->
                log.e(throwable) { "Failed to parse deeplink $rawUri" }
            }
            .map { uri ->
                val route = buildRoute(uri)
                val arguments = uri.queryParameterNames.associateWith { key ->
                    uri.getQueryParameter(key).orEmpty()
                }
                DeeplinkData(
                    rawUri = rawUri,
                    uri = uri,
                    route = route,
                    arguments = arguments,
                )
            }.getOrNull()
    }

    private fun buildRoute(uri: Uri): String {
        val host = uri.host?.takeIf { it.isNotBlank() }?.let { "$it/" } ?: ""
        val path = uri.pathSegments?.joinToString(separator = "/") ?: ""
        return (host + path).trim('/').ifEmpty { uri.path ?: uri.toString() }
    }
}
