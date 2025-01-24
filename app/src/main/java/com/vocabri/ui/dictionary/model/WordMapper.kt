package com.vocabri.ui.dictionary.model

import com.vocabri.domain.model.word.Word

// Extension function to map domain model to UI model
fun Word.toUiModel(): WordUiModel = WordUiModel(
    id = this.id,
    text = this.text,
    partOfSpeech = this.partOfSpeech.name,
    notes = this.notes,
    translations = this.translations.joinToString(", ") { it.text },
    examples = this.examples.joinToString(", ") { it.text },
)
