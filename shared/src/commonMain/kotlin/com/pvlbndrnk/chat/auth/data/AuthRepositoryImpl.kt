package com.pvlbndrnk.chat.auth.data

import com.pvlbndrnk.chat.auth.data.remote.AuthApi
import com.pvlbndrnk.chat.auth.data.remote.model.LoginRequestDto
import com.pvlbndrnk.chat.auth.data.remote.model.TokenRequestDto
import com.pvlbndrnk.chat.auth.data.remote.model.UserRequestDto
import com.pvlbndrnk.chat.auth.domain.AuthRepository
import com.pvlbndrnk.chat.auth.domain.error.AuthException
import com.pvlbndrnk.chat.session.SessionManager
import kotlin.coroutines.cancellation.CancellationException

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val sessionManager: SessionManager,
): AuthRepository {
    override suspend fun register(username: String, password: String) {
        val request = UserRequestDto(username, password)
        authApi.register(request)
    }

    override suspend fun login(username: String, password: String) {
        val request = LoginRequestDto(username, password)
        val response = authApi.login(request)
        sessionManager.startSession(
            user = response.user.toUser(),
            accessToken = response.accessToken,
            refreshToken = response.refreshToken
        )
    }

    override suspend fun refreshAccessToken() {
        try {
            val refreshToken = sessionManager.readRefreshToken()
            if (refreshToken == null) {
                sessionManager.markUnauthenticated()
                return
            }
            try {
                val response = authApi.refreshAccessToken(
                    TokenRequestDto(refreshToken)
                )
                sessionManager.setAccessToken(
                    user = response.user.toUser(),
                    accessToken = response.token,
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