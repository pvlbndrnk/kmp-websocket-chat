package com.pvlbndrnk.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pvlbndrnk.chat.auth.presentation.AuthScreen
import com.pvlbndrnk.chat.di.appModule
import com.pvlbndrnk.chat.presentation.chat.ChatScreen
import com.pvlbndrnk.chat.session.SessionState
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.Module
import org.koin.dsl.KoinConfiguration

@Composable
fun App(
    additionalModules: List<Module> = emptyList(),
    serverBaseUrl: String = "https://pvlbndrnk.duckdns.org/",
) {
    KoinApplication(
        configuration = KoinConfiguration(
            config = {
                modules(appModule(serverBaseUrl))
                modules(additionalModules)
            }
        )
    ) {
        MaterialTheme {
            val appViewModel: AppViewModel = koinViewModel()
            val sessionState by appViewModel.sessionState.collectAsStateWithLifecycle()
            SessionContent(sessionState, appViewModel::retryRestore)
        }
    }
}

@Composable
private fun SessionContent(
    state: SessionState,
    onRetry: () -> Unit,
) {
    when(state) {
        SessionState.Restoring -> RestoringScreen()
        SessionState.RestoreFailed -> RestoringFailedScreen(onRetry)
        SessionState.Unauthenticated -> AuthScreen()
        is SessionState.Authenticated -> ChatScreen(username = state.user.username)
    }
}

@Composable
private fun RestoringScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun RestoringFailedScreen(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Couldn't restore your session")
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}
