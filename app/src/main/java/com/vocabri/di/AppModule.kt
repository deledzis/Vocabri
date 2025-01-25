package com.vocabri.di

import com.vocabri.domain.model.kover.ExcludeFromCoverage
import com.vocabri.ui.addword.viewmodel.AddWordViewModel
import com.vocabri.ui.dictionary.viewmodel.DictionaryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

@ExcludeFromCoverage
val appModule = module {
    viewModelOf(::DictionaryViewModel)
    viewModelOf(::AddWordViewModel)

    factory { SupervisorJob() }
    factory { CoroutineScope(Dispatchers.IO + SupervisorJob()) }
}
