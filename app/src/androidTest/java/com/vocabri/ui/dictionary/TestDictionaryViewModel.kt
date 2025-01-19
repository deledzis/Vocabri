package com.vocabri.ui.dictionary

import com.vocabri.ui.dictionary.viewmodel.DictionaryState
import com.vocabri.ui.dictionary.viewmodel.DictionaryViewModel
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TestDictionaryViewModel : DictionaryViewModel(
    getWordsUseCase = mockk(),
    deleteWordUseCase = mockk()
) {
    private val _state = MutableStateFlow<DictionaryState>(DictionaryState.Empty)
    override val state: StateFlow<DictionaryState> = _state

    fun setState(newState: DictionaryState) {
        _state.value = newState
    }
}
