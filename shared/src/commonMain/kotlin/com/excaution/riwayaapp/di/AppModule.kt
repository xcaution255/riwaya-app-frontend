package com.excaution.riwayaapp.di

import androidx.lifecycle.viewmodel.CreationExtras.Empty.get
import com.excaution.riwayaapp.presentation.navigation.bottombar.BottomViewModel
import kotlinx.coroutines.NonCancellable.get
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel
import kotlin.coroutines.EmptyCoroutineContext.get


val appModule = module {
//    single {
//
//        HttpClientFactory.create()
//
//    }
//
//    single<ApiService> {
//
//        ApiServiceImpl(get())
//
//    }
//
//    single<UserRepository> {
//
//        UserRepositoryImpl(get())
//
//    }
//    //
//    single<AuthApi>{
//
//        AuthApiImpl(get())
//
//    }
//    single<AuthRepository>{
//
//        AuthRepositoryImpl(get())
//
//    }
//
    viewModel {
        BottomViewModel()
    }

}