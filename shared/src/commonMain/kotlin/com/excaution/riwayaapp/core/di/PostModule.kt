package com.excaution.riwayaapp.core.di

import com.excaution.riwayaapp.data.post.PostApi
import com.excaution.riwayaapp.data.post.PostRepository
import com.excaution.riwayaapp.presentation.home.PostViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val postModule = module {
    single { PostApi(get()) }
    single { PostRepository(get()) }
    viewModel { PostViewModel(get(), get()) }
}