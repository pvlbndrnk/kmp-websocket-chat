package com.pvlbndrnk.chat.auth.data.remote

import com.pvlbndrnk.chat.auth.domain.error.AuthException
import com.pvlbndrnk.chat.auth.data.remote.model.TokenRequestDto
import com.pvlbndrnk.chat.auth.data.remote.model.TokenResponseDto
import com.pvlbndrnk.chat.auth.data.remote.model.LoginRequestDto
import com.pvlbndrnk.chat.auth.data.remote.model.LoginResponseDto
import com.pvlbndrnk.chat.auth.data.remote.model.UserRequestDto
import com.pvlbndrnk.chat.auth.data.remote.model.UserResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class AuthApiImpl(private val httpClient: HttpClient): AuthApi {
    override suspend fun register(request: UserRequestDto): UserResponseDto {
        val response = httpClient.post("auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return when (response.status) {
            HttpStatusCode.Created -> response.body()
            HttpStatusCode.Conflict -> throw AuthException.UserAlreadyExists()
            else -> throw IllegalStateException("Registration failed: HTTP ${response.status}")
        }
    }

    override suspend fun login(request: LoginRequestDto): LoginResponseDto {
        val response = httpClient.post("auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return when(response.status) {
            HttpStatusCode.Created -> response.body()
            HttpStatusCode.Unauthorized -> throw AuthException.InvalidCredentials()
            else -> throw IllegalStateException("Login failed: HTTP ${response.status}")
        }
    }

    override suspend fun refreshAccessToken(request: TokenRequestDto): TokenResponseDto {
        val response = httpClient.post("auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return when(response.status) {
            HttpStatusCode.Created -> response.body()
            HttpStatusCode.Unauthorized -> throw AuthException.InvalidToken()
            else -> throw IllegalStateException("Create accessToken failed: HTTP ${response.status}")
        }
    }
}