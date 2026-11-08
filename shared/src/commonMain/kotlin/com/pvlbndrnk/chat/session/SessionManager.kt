package com.pvlbndrnk.chat.session

import com.pvlbndrnk.chat.core.user.model.User
import kotlinx.coroutines.flow.StateFlow

interface SessionManager {

    val accessToken: String?
    val sessionState: StateFlow<SessionState>

    suspend fun startSession(user: User, accessToken: String, refreshToken: String)
    suspend fun clearSession()
    suspend fun readRefreshToken(): String?
    fun setAccessToken(user: User, accessToken: String)
    fun markUnauthenticated()
    fun markRestoring()
    fun markRestoreFailed()
}