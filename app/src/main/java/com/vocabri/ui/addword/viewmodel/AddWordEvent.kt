package com.vocabri.ui.addword.viewmodel

import com.vocabri.domain.model.word.PartOfSpeech

sealed class AddWordEvent {
    data class UpdateText(val text: String) : AddWordEvent()

    data class UpdateCurrentTranslation(val translation: String) : AddWordEvent()

    data object AddTranslation : AddWordEvent()

    data class RemoveTranslation(val translation: String) : AddWordEvent()

    data class OnTranslationFieldFocusChange(val focused: Boolean) : AddWordEvent()

    data class UpdatePartOfSpeech(val partOfSpeech: PartOfSpeech) : AddWordEvent()

    data object SaveWord : AddWordEvent()
}
