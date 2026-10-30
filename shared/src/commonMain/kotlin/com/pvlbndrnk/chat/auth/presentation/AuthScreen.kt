package com.pvlbndrnk.chat.auth.presentation

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val uiState = viewModel.uiState.value
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .pointerInput(focusManager) {
                detectTapGestures(onTap = { focusManager.clearFocus() })
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Header(authMode = uiState.authMode)
        InputUsername(uiState, viewModel::onUsernameChange)
        InputPassword(uiState, viewModel::onSubmit, viewModel::onPasswordChange)
        SubmitButton(uiState.authMode, uiState.isSubmitting, viewModel::onSubmit)
        SwitchAuthMode(uiState.authMode, viewModel::onAuthModeChange)
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
        supportingText = { state.usernameError?.let { Text(text = it) } },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        isError = state.usernameError != null,
        singleLine = true,
    )
}

@Composable
private fun InputPassword(
    state: AuthUiState,
    onSubmit: () -> Unit,
    onPasswordChange: (String) -> Unit,
) {
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
        supportingText = { state.passwordError?.let { Text(text = it) } },
        trailingIcon = {
            TextButton(onClick = { isPasswordHidden = !isPasswordHidden }) {
                Text(text = if (isPasswordHidden) "Show" else "Hide")
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onSubmit.invoke() }),
        isError = state.passwordError != null,
        singleLine = true,
    )
}

@Composable
private fun SubmitButton(authMode: AuthMode, isSubmitting: Boolean, onSubmit: () -> Unit) {
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