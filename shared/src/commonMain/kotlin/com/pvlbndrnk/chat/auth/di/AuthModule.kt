package com.pvlbndrnk.chat.auth.di

import com.pvlbndrnk.chat.auth.data.AuthRepositoryImpl
import com.pvlbndrnk.chat.auth.data.remote.AuthApi
import com.pvlbndrnk.chat.auth.data.remote.AuthApiImpl
import com.pvlbndrnk.chat.auth.domain.AuthRepository
import com.pvlbndrnk.chat.auth.presentation.AuthViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    single<AuthApi> { AuthApiImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }

    viewModel { AuthViewModel(get()) }
}