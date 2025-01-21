package com.vocabri.test.ui.dictionary

import com.vocabri.ui.dictionary.viewmodel.DictionaryState
import com.vocabri.ui.dictionary.viewmodel.DictionaryViewModel
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TestDictionaryViewModel : DictionaryViewModel(
    getWordsUseCase = mockk(),
    deleteWordUseCase = mockk(),
    ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    private val _state = MutableStateFlow<DictionaryState>(DictionaryState.Empty)
    override val state: StateFlow<DictionaryState> = _state

    fun setState(newState: DictionaryState) {
        _state.value = newState
    }
}
