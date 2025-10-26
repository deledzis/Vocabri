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
package com.vocabri.notifications.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MainThread
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.vocabri.logger.logger
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Handles runtime notification permission logic for Android 13 and above.
 */
class NotificationPermissionHandler(private val context: Context) {
    private val log = logger()

    fun status(activity: Activity? = null): NotificationPermissionStatus {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
            if (granted) {
                return NotificationPermissionStatus.Granted
            }
            val shouldShowRationale = activity?.let {
                ActivityCompat.shouldShowRequestPermissionRationale(it, Manifest.permission.POST_NOTIFICATIONS)
            } ?: false
            return if (shouldShowRationale) {
                NotificationPermissionStatus.Denied
            } else {
                NotificationPermissionStatus.PermanentlyDenied
            }
        }
        val notificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
        return if (notificationsEnabled) {
            NotificationPermissionStatus.Granted
        } else {
            NotificationPermissionStatus.DisabledFromSystemSettings
        }
    }

    fun shouldShowRationale(activity: Activity): Boolean = ActivityCompat.shouldShowRequestPermissionRationale(
        activity,
        Manifest.permission.POST_NOTIFICATIONS,
    )

    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(intent) }
            .onFailure { throwable -> log.e(throwable) { "Unable to open app settings" } }
    }

    fun registerPermissionLauncher(
        activity: ComponentActivity,
        onResult: (Boolean) -> Unit,
    ): NotificationPermissionLauncher {
        val launcher = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            log.i { "Notification permission result: $granted" }
            onResult(granted)
        }
        return NotificationPermissionLauncher(launcher)
    }

    suspend fun awaitPermission(activity: ComponentActivity): Boolean = suspendCancellableCoroutine { continuation ->
        val launcher = registerPermissionLauncher(activity) { granted ->
            if (continuation.isActive) {
                continuation.resume(granted)
            }
        }
        continuation.invokeOnCancellation { launcher.dispose() }
        launcher.launch()
    }
}

sealed interface NotificationPermissionStatus {
    data object Granted : NotificationPermissionStatus
    data object Denied : NotificationPermissionStatus
    data object PermanentlyDenied : NotificationPermissionStatus
    data object DisabledFromSystemSettings : NotificationPermissionStatus
}

class NotificationPermissionLauncher internal constructor(
    private val launcher: ActivityResultLauncher<String>,
) {
    private var launched = false

    @MainThread
    fun launch() {
        if (!launched) {
            launched = true
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    fun dispose() {
        runCatching { launcher.unregister() }
    }
}
