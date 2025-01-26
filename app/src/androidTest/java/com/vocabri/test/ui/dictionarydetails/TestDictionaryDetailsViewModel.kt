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
package com.vocabri.test.ui.dictionarydetails

import com.vocabri.R
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.ui.dictionarydetails.viewmodel.DictionaryDetailsState
import com.vocabri.ui.dictionarydetails.viewmodel.DictionaryDetailsViewModel
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class TestDictionaryDetailsViewModel :
    DictionaryDetailsViewModel(
        getWordsUseCase = mockk(),
        deleteWordUseCase = mockk(),
        ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
    ) {
    private val _state = MutableStateFlow<DictionaryDetailsState>(
        DictionaryDetailsState.Empty(titleId = R.string.loading),
    )
    override val state: StateFlow<DictionaryDetailsState> = _state

    fun setState(newState: DictionaryDetailsState) {
        _state.update { newState }
    }

    fun setCurrentPartOfSpeechForTest(partOfSpeech: PartOfSpeech) {
        currentPartOfSpeech = partOfSpeech
    }
}
