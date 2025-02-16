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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocabri.domain.usecase.debug.GenerateRandomWordUseCase
import com.vocabri.domain.usecase.word.AddWordUseCase
import com.vocabri.logger.logger
import com.vocabri.ui.navigation.NavigationRoute
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val generateRandomWordUseCase: GenerateRandomWordUseCase,
    private val addWordsUseCase: AddWordUseCase,
    private val ioScope: CoroutineScope,
) : ViewModel() {
    private val log = logger()

    private val _state = MutableStateFlow(MainState(routes = bottomNavigationScreens))
    val state: StateFlow<MainState> = _state

    fun handleEvent(event: MainEvent) {
        log.i { "Handling event: $event" }
        when (event) {
            is MainEvent.OnPlusButtonLongClick -> onPlusButtonLongClick()
        }
    }

    private fun onPlusButtonLongClick() {
        val word = generateRandomWordUseCase.execute()
        log.d { "Word to save: $word" }
        viewModelScope.launch(ioScope.coroutineContext) {
            try {
                addWordsUseCase.execute(word)
                log.i { "Word saved successfully" }
            } catch (e: IllegalStateException) {
                log.e(e) { "Word already exists, ignore and leave: $e" }
            } catch (e: Exception) {
                log.e(e) { "Failed to save the word: $e" }
            }
        }
    }

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
