package com.vocabri.ui.dictionary.model

import com.vocabri.domain.model.word.PartOfSpeech

data class WordUi(
    val id: String,
    val text: String,
    val translations: String,
    val examples: List<String>,
    val partOfSpeech: PartOfSpeech,
    val notes: String? = null
)
