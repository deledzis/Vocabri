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
package com.vocabri.ui.main.viewmodel

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocabri.BuildConfig
import com.vocabri.domain.usecase.debug.GenerateRandomWordUseCase
import com.vocabri.domain.usecase.word.AddWordUseCase
import com.vocabri.logger.logger
import com.vocabri.ui.navigation.NavigationRoute
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val generateRandomWordUseCase: GenerateRandomWordUseCase,
    private val addWordsUseCase: AddWordUseCase,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val log = logger()

    private val _uiState = MutableStateFlow(MainContract.UiState(routes = bottomNavigationScreens))
    val uiState: StateFlow<MainContract.UiState> = _uiState

    private val _sideEffect = Channel<MainContract.UiEffect>()
    val sideEffect: Flow<MainContract.UiEffect> = _sideEffect.receiveAsFlow()

    fun onEvent(event: MainContract.UiEvent) {
        log.i { "Handling event: $event" }
        when (event) {
            is MainContract.UiEvent.OnPlusButtonLongClick -> onPlusButtonLongClick()
        }
    }

    private fun onPlusButtonLongClick() {
        if (!BuildConfig.DEBUG || !isEmulator()) {
            log.w {
                "Random word generation is only available in debug builds on emulator," +
                    " is debug: ${BuildConfig.DEBUG}, is emulator: ${isEmulator()}"
            }
            return
        }

        val word = generateRandomWordUseCase.execute()
        log.d { "Word to save: $word" }
        viewModelScope.launch(ioDispatcher) {
            try {
                addWordsUseCase.execute(word)
                log.i { "Word saved successfully" }
                _sideEffect.send(MainContract.UiEffect.ShowWordAddedSuccess)
            } catch (e: IllegalStateException) {
                log.e(e) { "Word already exists, ignore and leave: $e" }
                _sideEffect.send(MainContract.UiEffect.ShowWordAlreadyExists)
            } catch (e: Exception) {
                log.e(e) { "Failed to save the word: $e" }
                _sideEffect.send(MainContract.UiEffect.ShowError("Failed to save the word"))
            }
        }
    }

    // TODO move out
    private fun isEmulator(): Boolean = (
        Build.FINGERPRINT.startsWith("generic") ||
            Build.FINGERPRINT.startsWith("unknown") ||
            Build.MODEL.contains("google_sdk") ||
            Build.MODEL.contains("sdk_gphone") ||
            Build.MODEL.contains("Emulator") ||
            Build.MODEL.contains("Android SDK built for x86") ||
            Build.MANUFACTURER.contains("Genymotion") ||
            Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic") ||
            "google_sdk" == Build.PRODUCT
        )

    companion object {
        private val bottomNavigationScreens = listOf(
            NavigationRoute.Start.Dictionary,
            NavigationRoute.Start.Training,
            NavigationRoute.Empty,
            NavigationRoute.Start.DiscoverMore,
            NavigationRoute.Start.Settings,
        )
    }
}
