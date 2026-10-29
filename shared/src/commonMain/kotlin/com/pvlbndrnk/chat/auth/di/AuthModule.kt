package com.pvlbndrnk.chat.auth.di

import com.pvlbndrnk.chat.auth.presentation.AuthViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    viewModel { AuthViewModel() }
}