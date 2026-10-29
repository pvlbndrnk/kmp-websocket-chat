package com.pvlbndrnk.chat.auth.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class AuthViewModel: ViewModel() {

    private val _uiState = mutableStateOf(AuthUiState())
    val uiState: State<AuthUiState> = _uiState

    fun onUsernameChange(username: String) {
        _uiState.value = _uiState.value.copy(
            username = username
        )
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password
        )
    }

    fun onAuthModeChange(authMode: AuthMode) {
        _uiState.value = _uiState.value.copy(
            authMode = authMode,
            password = ""
        )
    }
}