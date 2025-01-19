package com.vocabri.domain.model.word

data class Word(
    val id: String,
    val text: String,
    val translations: List<String>,
    val examples: List<String>,
    val partOfSpeech: PartOfSpeech,
    val notes: String? = null
)
