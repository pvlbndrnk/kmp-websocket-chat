package com.pvlbndrnk.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvlbndrnk.chat.auth.domain.AuthRepository
import com.pvlbndrnk.chat.session.SessionManager
import com.pvlbndrnk.chat.session.SessionState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {
    val sessionState: StateFlow<SessionState> = sessionManager.sessionState

    init {
        retryRestore()
    }

    fun retryRestore() {
        sessionManager.markRestoring()
        viewModelScope.launch {
            authRepository.refreshAccessToken()
        }
    }
}