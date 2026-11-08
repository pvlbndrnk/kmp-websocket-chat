package com.pvlbndrnk.chat.auth.data

import com.pvlbndrnk.chat.auth.data.remote.AuthApi
import com.pvlbndrnk.chat.auth.data.remote.model.CreateAccessTokenRequestDto
import com.pvlbndrnk.chat.auth.data.remote.model.CreateSessionRequestDto
import com.pvlbndrnk.chat.auth.data.remote.model.CreateUserRequestDto
import com.pvlbndrnk.chat.auth.domain.AuthRepository
import com.pvlbndrnk.chat.auth.domain.error.AuthException
import com.pvlbndrnk.chat.session.SessionManager
import kotlin.coroutines.cancellation.CancellationException

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val sessionManager: SessionManager,
): AuthRepository {
    override suspend fun register(username: String, password: String) {
        val request = CreateUserRequestDto(username, password)
        authApi.createUser(request)
    }

    override suspend fun login(username: String, password: String) {
        val request = CreateSessionRequestDto(username, password)
        val response = authApi.createSession(request)
        sessionManager.startSession(
            user = response.user.toUser(),
            accessToken = response.accessToken,
            refreshToken = response.refreshToken
        )
    }

    override suspend fun restoreSession() {
        try {
            val refreshToken = sessionManager.readRefreshToken()
            if (refreshToken == null) {
                sessionManager.markUnauthenticated()
                return
            }
            try {
                val response = authApi.createAccessToken(
                    CreateAccessTokenRequestDto(refreshToken)
                )
                sessionManager.setAccessToken(
                    user = response.user.toUser(),
                    accessToken = response.accessToken,
                )
            } catch (_: AuthException.InvalidToken) {
                sessionManager.clearSession()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            sessionManager.markRestoreFailed()
        }
    }
}