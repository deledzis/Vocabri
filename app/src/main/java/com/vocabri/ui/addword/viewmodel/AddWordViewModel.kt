package com.vocabri.ui.addword.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocabri.R
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.usecase.word.AddWordUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddWordViewModel(
    private val addWordsUseCase: AddWordUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<AddWordState>(AddWordState.Editing())
    val state: StateFlow<AddWordState> = _state

    private fun isSaveButtonEnabled(
        text: String? = null,
        currentTranslation: String? = null,
        translations: List<String>? = null,
    ): Boolean = stateAsEditing()?.let { currentState ->
        val wordNotEmpty = text?.isNotBlank() ?: currentState.text.isNotBlank()
        val translationsNotEmpty =
            translations?.isNotEmpty() ?: currentState.translations.isNotEmpty()
        val currentTranslationNotEmpty =
            currentTranslation?.isNotBlank() ?: currentState.currentTranslation.isNotBlank()
        wordNotEmpty && (translationsNotEmpty || currentTranslationNotEmpty)
    } ?: false

    fun handleEvent(event: AddWordEvent) {
        when (event) {
            is AddWordEvent.UpdateText -> updateText(event.text)
            AddWordEvent.AddTranslation -> addTranslation()
            is AddWordEvent.RemoveTranslation -> removeTranslation(event.translation)
            is AddWordEvent.UpdateCurrentTranslation -> updateCurrentTranslation(event.translation)
            is AddWordEvent.OnTranslationFieldFocusChange -> onTranslationFieldFocusChange(event.focused)
            is AddWordEvent.UpdatePartOfSpeech -> updatePartOfSpeech(event.partOfSpeech)
            AddWordEvent.SaveWord -> saveWord()
        }
    }

    private fun updateText(text: String) = stateAsEditing()?.let { currentState ->
        _state.value = currentState.copy(
            text = text,
            isSaveButtonEnabled = isSaveButtonEnabled(text = text),
        )
    }

    private fun addTranslation() = stateAsEditing()?.let { currentState ->
        if (currentState.currentTranslation.isNotBlank()) {
            val newTranslation = currentState.currentTranslation.trim()
            _state.value = currentState.copy(
                translations = currentState.translations + newTranslation,
                currentTranslation = "",
                showAddTranslationButton = false,
                isSaveButtonEnabled = isSaveButtonEnabled(currentTranslation = newTranslation),
            )
        }
    }

    private fun removeTranslation(translation: String) = stateAsEditing()?.let { currentState ->
        val updatedTranslations =
            currentState.translations.toMutableList().apply { remove(translation) }
        _state.value = currentState.copy(
            translations = updatedTranslations,
            isSaveButtonEnabled = isSaveButtonEnabled(translations = updatedTranslations),
        )
    }

    private fun updateCurrentTranslation(translation: String) =
        stateAsEditing()?.let { currentState ->
            _state.value = currentState.copy(
                currentTranslation = translation,
                showAddTranslationButton = translation.isNotBlank() && currentState.isTranslationFieldFocused,
                isSaveButtonEnabled = isSaveButtonEnabled(currentTranslation = translation),
            )
        }

    private fun onTranslationFieldFocusChange(focused: Boolean) =
        stateAsEditing()?.let { currentState ->
            _state.value = currentState.copy(
                isTranslationFieldFocused = focused,
                showAddTranslationButton = focused && currentState.currentTranslation.isNotBlank()
            )
        }

    private fun updatePartOfSpeech(partOfSpeech: PartOfSpeech) =
        stateAsEditing()?.let { currentState ->
            _state.value = currentState.copy(partOfSpeech = partOfSpeech)
        }

    private fun saveWord() = stateAsEditing()?.let { currentState ->
        // Save unsaved translation if present
        val finalTranslations = if (currentState.currentTranslation.isNotBlank()) {
            currentState.translations + currentState.currentTranslation.trim()
        } else {
            currentState.translations
        }

        if (currentState.text.isBlank() || finalTranslations.isEmpty()) {
            _state.value = currentState.copy(errorMessageId = R.string.add_word_empty_field)
            return@let
        }

        val word = Word(
            id = System.currentTimeMillis().toString(),
            text = currentState.text.trim(),
            translations = finalTranslations,
            examples = emptyList(),
            partOfSpeech = currentState.partOfSpeech,
            notes = null
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                addWordsUseCase.execute(word)
                _state.value = AddWordState.Saved
            } catch (e: Exception) {
                _state.value = AddWordState.Error("Failed to save the word")
            }
        }

        // Logic to save the word
        _state.value = AddWordState.Saved
    }

    private fun stateAsEditing() = (_state.value as? AddWordState.Editing)
}
