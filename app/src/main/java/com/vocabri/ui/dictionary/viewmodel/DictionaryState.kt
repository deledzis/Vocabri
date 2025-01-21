package com.vocabri.ui.dictionary.viewmodel

import com.vocabri.ui.dictionary.model.WordUiModel

sealed class DictionaryState {
    data object Empty : DictionaryState()
    data object Loading : DictionaryState()
    data class WordsLoaded(val words: List<WordUiModel>) : DictionaryState()
    data class Error(val message: String) : DictionaryState()
}
