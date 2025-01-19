package com.vocabri.ui.dictionary.viewmodel

sealed class DictionaryEvent {
    data object LoadWords : DictionaryEvent()
    data object AddWordClicked : DictionaryEvent()
    data class DeleteWordClicked(val id: String) : DictionaryEvent()
}
