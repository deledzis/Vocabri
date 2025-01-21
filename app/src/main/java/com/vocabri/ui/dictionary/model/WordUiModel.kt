package com.vocabri.ui.dictionary.model

data class WordUiModel(
    val id: String,
    val text: String,
    val translations: String,
    val examples: String,
    val partOfSpeech: String,
    val notes: String?,
)
