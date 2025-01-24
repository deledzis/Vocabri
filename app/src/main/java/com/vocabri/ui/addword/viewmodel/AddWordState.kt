package com.vocabri.ui.addword.viewmodel

import androidx.annotation.StringRes
import com.vocabri.domain.model.word.PartOfSpeech

sealed class AddWordState {
    data class Editing(
        val text: String = "",
        val translations: List<String> = emptyList(),
        val currentTranslation: String = "",
        val isTranslationFieldFocused: Boolean = false,
        val showAddTranslationButton: Boolean = false,
        val partOfSpeech: PartOfSpeech = PartOfSpeech.NOUN,
        val isSaveButtonEnabled: Boolean = false,
        @StringRes
        val errorMessageId: Int? = null,
    ) : AddWordState()

    data object Saved : AddWordState()

    data class Error(val message: String) : AddWordState()
}
