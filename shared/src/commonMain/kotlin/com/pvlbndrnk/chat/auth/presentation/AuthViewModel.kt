package com.pvlbndrnk.chat.auth.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AuthViewModel: ViewModel() {

    private val _uiState = mutableStateOf(AuthUiState())
    val uiState: State<AuthUiState> = _uiState

    fun onUsernameChange(username: String) {
        _uiState.value = _uiState.value.copy(
            username = username,
            usernameError = null,
        )
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = null,
        )
    }

    fun onAuthModeChange(authMode: AuthMode) {
        _uiState.value = _uiState.value.copy(
            authMode = authMode,
            password = "",
            usernameError = null,
            passwordError = null,
        )
    }

    fun onSubmit() {
        _uiState.value = _uiState.value.copy(
            usernameError = if (_uiState.value.username.isBlank()) {
                "Enter a username"
            } else {
                null
            },
            passwordError = if (_uiState.value.password.isBlank()) {
                "Enter a password"
            } else {
                null
            }
        )
    }
}