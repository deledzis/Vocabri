package com.vocabri.data.mapper

import com.vocabri.data.WordEntity
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.model.word.Word

// TODO: unit tests
class WordMapper {
    fun toDomainModel(wordEntity: WordEntity) = Word(
        id = wordEntity.id,
        text = wordEntity.text,
        translations = wordEntity.translations.split("¶").map { it },
        examples = wordEntity.examples?.split("¶")?.map { it } ?: emptyList(),
        partOfSpeech = PartOfSpeech.valueOf(wordEntity.partOfSpeech),
        notes = wordEntity.notes
    )

    fun toDatabaseModel(word: Word) = WordEntity(
        id = word.id,
        text = word.text,
        translations = word.translations.joinToString("¶") { it },
        examples = word.examples.joinToString("¶") { it },
        partOfSpeech = word.partOfSpeech.name,
        notes = word.notes
    )
}
