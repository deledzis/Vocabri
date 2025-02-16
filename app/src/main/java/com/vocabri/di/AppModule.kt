/*
 * MIT License
 *
 * Copyright (c) 2025 Aleksandr Stiagov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.vocabri.di

import com.vocabri.di.qualifiers.DictionaryDetailsQualifiers
import com.vocabri.domain.model.kover.ExcludeFromCoverage
import com.vocabri.domain.model.word.PartOfSpeech
import com.vocabri.domain.repository.ResourcesRepository
import com.vocabri.ui.main.viewmodel.MainViewModel
import com.vocabri.ui.screens.addword.viewmodel.AddWordViewModel
import com.vocabri.ui.screens.dictionary.viewmodel.DictionaryViewModel
import com.vocabri.ui.screens.dictionarydetails.viewmodel.DictionaryDetailsViewModel
import com.vocabri.utils.AndroidResourcesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

@ExcludeFromCoverage
val appModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::DictionaryViewModel)
    viewModelOf(::AddWordViewModel)

    /* Dictionary VMs for different parts of speech */
    PartOfSpeech.entries.forEach { partOfSpeech ->
        viewModel(qualifier = DictionaryDetailsQualifiers.fromPartOfSpeech(partOfSpeech)) {
            DictionaryDetailsViewModel(
                partOfSpeech = partOfSpeech,
                observeWordsUseCase = get(),
                deleteWordUseCase = get(),
                ioScope = get(),
            )
        }
    }

    factory { SupervisorJob() }
    factory { CoroutineScope(context = Dispatchers.IO + SupervisorJob()) }

    single<ResourcesRepository> { AndroidResourcesRepository(context = androidContext()) }
}
