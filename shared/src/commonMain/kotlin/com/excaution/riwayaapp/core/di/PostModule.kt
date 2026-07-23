package com.excaution.riwayaapp.core.di

import com.excaution.riwayaapp.data.post.PostApi
import com.excaution.riwayaapp.data.post.PostRepository
import com.excaution.riwayaapp.presentation.home.PostViewModel
import com.excaution.riwayaapp.presentation.saved.PostSavedViewModel
import com.excaution.riwayaapp.presentation.search.SearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val postModule = module {
    single { PostApi(get()) }
    single { PostRepository(get()) }
    viewModel { PostViewModel(get(), get()) }
    viewModel { PostSavedViewModel(get(), get()) }
    viewModel { SearchViewModel(get()) }
}