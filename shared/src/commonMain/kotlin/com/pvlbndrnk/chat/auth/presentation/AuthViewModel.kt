package com.pvlbndrnk.chat.auth.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvlbndrnk.chat.auth.domain.AuthRepository
import com.pvlbndrnk.chat.auth.domain.error.AuthException
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.log

class AuthViewModel(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _uiState = mutableStateOf(AuthUiState())
    val uiState: State<AuthUiState> = _uiState

    fun onUsernameChange(username: String) {
        _uiState.value = _uiState.value.copy(
            username = username,
            usernameError = null,
            submitError = null,
        )
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = null,
            submitError = null,
        )
    }

    fun onAuthModeChange(authMode: AuthMode) {
        _uiState.value = _uiState.value.copy(
            authMode = authMode,
            password = "",
            usernameError = null,
            passwordError = null,
            submitError = null,
        )
    }

    fun onSubmit() {
        val current = _uiState.value
        if (current.isSubmitting) return

        val usernameError = if (current.username.isBlank()) "Enter a username" else null
        val passwordError = if (current.password.isBlank()) "Enter a password" else null

        _uiState.value = current.copy(
            usernameError = usernameError,
            passwordError = passwordError,
            submitError = null,
        )

        if (usernameError != null || passwordError != null) return

        when (current.authMode) {
            AuthMode.REGISTER -> register(current.username, current.password)
            AuthMode.LOGIN -> login(current.username, current.password)
        }
    }

    private fun login(username: String, password: String) {
        _uiState.value = _uiState.value.copy(isSubmitting = true)

        viewModelScope.launch {
            try {
                authRepository.login(username, password)
            } catch (_: AuthException.InvalidCredentials) {
                _uiState.value = _uiState.value.copy(
                    submitError = "Invalid username or password",
                )
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(
                    submitError = "Could not log in. Try again.",
                )
            } finally {
                _uiState.value = _uiState.value.copy(isSubmitting = false)
            }
        }
    }

    private fun register(username: String, password: String) {
        _uiState.value = _uiState.value.copy(isSubmitting = true)

        viewModelScope.launch {
            try {
                authRepository.register(username, password)
                onAuthModeChange(AuthMode.LOGIN)
            } catch (_: AuthException.UserAlreadyExists) {
                _uiState.value = _uiState.value.copy(
                    usernameError = "Username already exists",
                )
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(
                    submitError = "Could not create account. Try again.",
                )
            } finally {
                _uiState.value = _uiState.value.copy(isSubmitting = false)
            }
        }
    }
}