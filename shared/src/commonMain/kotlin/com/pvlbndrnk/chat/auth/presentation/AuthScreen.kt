package com.pvlbndrnk.chat.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun AuthScreen(
    state: AuthUiState,
    onSubmit: () -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onAuthModeChange: (AuthMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Header(authMode = state.authMode)
        InputUsername(state, onUsernameChange)
        InputPassword(state, onPasswordChange)
        Submit(state.authMode, state.isSubmitting, onSubmit)
        SwitchAuthMode(state.authMode, onAuthModeChange)
    }
}

@Composable
private fun Header(authMode: AuthMode) {
    Text(
        text = when (authMode) {
            AuthMode.LOGIN -> "Login"
            AuthMode.REGISTER -> "Create account"
        }
    )
}

@Composable
private fun InputUsername(state: AuthUiState, onUsernameChange: (String) -> Unit) {
    TextField(
        value = state.username,
        onValueChange = onUsernameChange,
        label = { Text(text = "Username") },
        singleLine = true,
    )
}

@Composable
private fun InputPassword(state: AuthUiState, onPasswordChange: (String) -> Unit) {
    var isPasswordHidden by remember(state.authMode) {
        mutableStateOf(true)
    }
    TextField(
        value = state.password,
        onValueChange = onPasswordChange,
        visualTransformation = if (isPasswordHidden) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        label = { Text(text = "Password") },
        singleLine = true,
        trailingIcon = {
            TextButton(onClick = { isPasswordHidden = !isPasswordHidden }) {
                Text(text = if (isPasswordHidden) "Show" else "Hide")
            }
        }
    )
}

@Composable
private fun Submit(authMode: AuthMode, isSubmitting: Boolean, onSubmit: () -> Unit) {
    Button(onClick = onSubmit, enabled = !isSubmitting) {
        if (isSubmitting) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp))
        } else {
            Text(
                text = when (authMode) {
                    AuthMode.LOGIN -> "Login"
                    AuthMode.REGISTER -> "Create account"
                }
            )
        }
    }
}

@Composable
private fun SwitchAuthMode(authMode: AuthMode, onAuthModeChange: (AuthMode) -> Unit) {
    TextButton(onClick = {
        onAuthModeChange(
            when (authMode) {
                AuthMode.LOGIN -> AuthMode.REGISTER
                AuthMode.REGISTER -> AuthMode.LOGIN
            }
        )
    }) {
        Text(
            text = when (authMode) {
                AuthMode.LOGIN -> "Create account"
                AuthMode.REGISTER -> "Already have an account? Login."
            }
        )
    }
}