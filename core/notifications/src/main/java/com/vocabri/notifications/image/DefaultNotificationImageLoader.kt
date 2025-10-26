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
package com.vocabri.notifications.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.createBitmap
import com.vocabri.logger.logger
import com.vocabri.notifications.model.NotificationImage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/**
 * Default implementation that covers common image loading use-cases without pulling heavyweight dependencies.
 */
class DefaultNotificationImageLoader(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : NotificationImageLoader {

    private val log = logger()

    override suspend fun load(image: NotificationImage?): Bitmap? {
        if (image == null) return null
        return withContext(ioDispatcher) {
            runCatching {
                when (image) {
                    is NotificationImage.BitmapImage -> image.bitmap
                    is NotificationImage.Resource -> decodeResource(image.resId)
                    is NotificationImage.UriImage -> {
                        context.contentResolver.openInputStream(image.uri)
                            ?.use(BitmapFactory::decodeStream)
                    }

                    is NotificationImage.UrlImage -> image.download()
                }
            }.onFailure { throwable ->
                log.e(throwable) { "Failed to load notification image" }
            }.getOrNull()
        }
    }

    private fun NotificationImage.UrlImage.download(): Bitmap? {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            connectTimeout = CONNECTION_TIMEOUT
            readTimeout = CONNECTION_TIMEOUT
            doInput = true
        }
        return connection.useForBitmap()
    }

    private fun HttpURLConnection.useForBitmap(): Bitmap? = try {
        connect()
        if (responseCode in 200..299) {
            inputStream.use(BitmapFactory::decodeStream)
        } else {
            log.w { "Unexpected HTTP response code $responseCode for notification image" }
            null
        }
    } finally {
        disconnect()
    }

    private fun decodeResource(@DrawableRes resId: Int): Bitmap? {
        BitmapFactory.decodeResource(context.resources, resId)?.let { return it }
        val drawable = AppCompatResources.getDrawable(context, resId) ?: return null
        val width = (drawable.intrinsicWidth.takeIf { it > 0 } ?: DEFAULT_DRAWABLE_SIZE)
        val height = (drawable.intrinsicHeight.takeIf { it > 0 } ?: DEFAULT_DRAWABLE_SIZE)
        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    companion object {
        private const val CONNECTION_TIMEOUT = 5_000
        private const val DEFAULT_DRAWABLE_SIZE = 256
    }
}
