package com.vocabri.di

import com.vocabri.domain.usecase.word.GetWordsUseCase
import com.vocabri.fake.FakeGetWordsUseCase
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val testModule = module {
    singleOf(::FakeGetWordsUseCase) { bind<GetWordsUseCase>() }
}