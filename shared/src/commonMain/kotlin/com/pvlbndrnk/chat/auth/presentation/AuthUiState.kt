package com.pvlbndrnk.chat.auth.presentation

data class AuthUiState(
    val username: String = "",
    val password: String = "",
    val authMode: AuthMode = AuthMode.LOGIN,
    val isSubmitting: Boolean = false,
    val usernameError: String? = null,
    val passwordError: String? = null,
)
