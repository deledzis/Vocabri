package com.vocabri.ui.dictionary.viewmodel

import com.vocabri.ui.dictionary.model.WordUi

sealed class DictionaryState {
    data object Empty : DictionaryState()
    data object Loading : DictionaryState()
    data class WordsLoaded(val words: List<WordUi>) : DictionaryState()
    data class Error(val message: String) : DictionaryState()
}
