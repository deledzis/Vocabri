package com.vocabri.ui.addword.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vocabri.R
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Translation
import com.vocabri.domain.model.word.Word
import com.vocabri.domain.usecase.word.AddWordUseCase
import com.vocabri.logger.logger
import com.vocabri.utils.IdGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddWordViewModel(
    private val addWordsUseCase: AddWordUseCase,
    private val idGenerator: IdGenerator,
    private val ioScope: CoroutineScope,
) : ViewModel() {
    private val log = logger()

    private val _state = MutableStateFlow<AddWordState>(AddWordState.Editing())
    val state: StateFlow<AddWordState> = _state

    fun handleEvent(event: AddWordEvent) {
        log.i { "Handling event: $event" }
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
        log.d { "Current text updated to: $text" }
        _state.update {
            currentState.copy(
                text = text,
                isSaveButtonEnabled = isSaveButtonEnabled(text = text),
            )
        }
    }

    private fun addTranslation() = stateAsEditing()?.let { currentState ->
        if (currentState.currentTranslation.isNotBlank()) {
            val newTranslation = currentState.currentTranslation.trim()
            log.d { "Adding new translation: $newTranslation" }
            _state.update {
                currentState.copy(
                    translations = currentState.translations + newTranslation,
                    currentTranslation = "",
                    showAddTranslationButton = false,
                    isSaveButtonEnabled = isSaveButtonEnabled(currentTranslation = newTranslation),
                )
            }
        }
    }

    private fun removeTranslation(translation: String) = stateAsEditing()?.let { currentState ->
        log.d { "Removing translation: $translation" }
        val updatedTranslations =
            currentState.translations.toMutableList().apply { remove(translation) }
        _state.update {
            currentState.copy(
                translations = updatedTranslations,
                isSaveButtonEnabled = isSaveButtonEnabled(translations = updatedTranslations),
            )
        }
    }

    private fun updateCurrentTranslation(translation: String) = stateAsEditing()?.let { currentState ->
        log.d { "Current translation updated to: $translation" }
        _state.update {
            currentState.copy(
                currentTranslation = translation,
                showAddTranslationButton = translation.isNotBlank() && currentState.isTranslationFieldFocused,
                isSaveButtonEnabled = isSaveButtonEnabled(currentTranslation = translation),
            )
        }
    }

    private fun onTranslationFieldFocusChange(focused: Boolean) = stateAsEditing()?.let { currentState ->
        log.d { "Translation field focus changed to: $focused" }
        _state.update {
            currentState.copy(
                isTranslationFieldFocused = focused,
                showAddTranslationButton = focused && currentState.currentTranslation.isNotBlank(),
            )
        }
    }

    private fun updatePartOfSpeech(partOfSpeech: PartOfSpeech) = stateAsEditing()?.let { currentState ->
        log.d { "Part of speech updated to: $partOfSpeech" }
        _state.update {
            currentState.copy(partOfSpeech = partOfSpeech)
        }
    }

    private fun saveWord() = stateAsEditing()?.let { currentState ->
        log.i { "Initiating saveWord process" }
        val finalTranslations = if (currentState.currentTranslation.isNotBlank()) {
            log.d { "Saving unsaved translation: ${currentState.currentTranslation.trim()}" }
            currentState.translations + currentState.currentTranslation.trim()
        } else {
            currentState.translations
        }

        if (currentState.text.isBlank() || finalTranslations.isEmpty()) {
            log.w { "Cannot save word: Empty text or translations" }
            _state.update { currentState.copy(errorMessageId = R.string.add_word_empty_field) }
            return@let
        }

        val word = Word(
            id = idGenerator.generateStringId(),
            text = currentState.text.trim(),
            translations = finalTranslations.map {
                Translation(
                    id = idGenerator.generateStringId(),
                    text = it,
                )
            },
            examples = emptyList(),
            partOfSpeech = currentState.partOfSpeech,
            notes = null,
        )

        log.d { "Word to save: $word" }
        viewModelScope.launch(ioScope.coroutineContext) {
            try {
                addWordsUseCase.execute(word)
                log.i { "Word saved successfully" }
                _state.update { AddWordState.Saved }
            } catch (e: Exception) {
                log.e(e) { "Failed to save the word" }
                _state.update { AddWordState.Error("Failed to save the word") }
            }
        }
    }

    private fun stateAsEditing() = (_state.value as? AddWordState.Editing)

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
}
