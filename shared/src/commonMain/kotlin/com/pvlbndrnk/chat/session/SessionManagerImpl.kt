package com.pvlbndrnk.chat.session

import com.pvlbndrnk.chat.core.user.model.User
import com.pvlbndrnk.chat.session.data.local.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManagerImpl(
    private val sessionStore: SessionStore,
) : SessionManager {

    private val _accessToken = MutableStateFlow<String?>(null)
    override val accessToken: String?
        get() = _accessToken.value
    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Restoring)
    override val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    override suspend fun startSession(user: User, accessToken: String, refreshToken: String) {
        sessionStore.write(refreshToken)
        setAccessToken(user, accessToken)
    }

    override suspend fun clearSession() {
        sessionStore.clear()
        markUnauthenticated()
    }

    override suspend fun readRefreshToken(): String? = sessionStore.read()

    override fun setAccessToken(user: User, accessToken: String) {
        _accessToken.value = accessToken
        _sessionState.value = SessionState.Authenticated(user)
    }

    override fun markUnauthenticated() {
        _accessToken.value = null
        _sessionState.value = SessionState.Unauthenticated
    }

    override fun markRestoring() {
        _accessToken.value = null
        _sessionState.value = SessionState.Restoring
    }

    override fun markRestoreFailed() {
        _accessToken.value = null
        _sessionState.value = SessionState.RestoreFailed
    }
}
