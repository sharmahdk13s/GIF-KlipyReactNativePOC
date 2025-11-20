package com.klipy.presentation.di

import com.klipy.presentation.algorithm.MasonryMeasurementsCalculator
import com.klipy.presentation.features.conversation.ConversationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module{
    viewModel { (conversationId: String?) ->
        ConversationViewModel(conversationId, get(), get())
    }

    single { MasonryMeasurementsCalculator }
}