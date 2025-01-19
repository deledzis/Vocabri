package com.vocabri.domain.di

import com.vocabri.domain.usecase.word.AddWordUseCase
import com.vocabri.domain.usecase.word.DeleteWordUseCase
import com.vocabri.domain.usecase.word.GetWordsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    // Use Cases
    factoryOf(::AddWordUseCase)
    factoryOf(::GetWordsUseCase)
    factoryOf(::DeleteWordUseCase)
}
