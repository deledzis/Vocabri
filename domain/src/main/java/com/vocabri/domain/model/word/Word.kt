package com.vocabri.domain.model.word

data class Word(
    val id: String,
    val text: String,
    val translations: List<Translation>,
    val examples: List<Example>,
    val partOfSpeech: PartOfSpeech,
    val notes: String? = null,
)
