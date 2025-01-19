package com.vocabri.di

import com.vocabri.ui.addword.viewmodel.AddWordViewModel
import com.vocabri.ui.dictionary.viewmodel.DictionaryViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::DictionaryViewModel)
    viewModelOf(::AddWordViewModel)
}
