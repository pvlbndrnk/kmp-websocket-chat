package com.pvlbndrnk.chat.auth.data.remote

import com.pvlbndrnk.chat.auth.domain.error.AuthException
import com.pvlbndrnk.chat.auth.data.remote.model.CreateAccessTokenRequestDto
import com.pvlbndrnk.chat.auth.data.remote.model.CreateAccessTokenResponseDto
import com.pvlbndrnk.chat.auth.data.remote.model.CreateSessionRequestDto
import com.pvlbndrnk.chat.auth.data.remote.model.CreateSessionResponseDto
import com.pvlbndrnk.chat.auth.data.remote.model.CreateUserRequestDto
import com.pvlbndrnk.chat.auth.data.remote.model.UserResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class AuthApiImpl(private val httpClient: HttpClient): AuthApi {
    override suspend fun createUser(request: CreateUserRequestDto): UserResponseDto {
        val response = httpClient.post("users") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return when (response.status) {
            HttpStatusCode.Created -> response.body()
            HttpStatusCode.Conflict -> throw AuthException.UserAlreadyExists()
            else -> throw IllegalStateException("Registration failed: HTTP ${response.status}")
        }
    }

    override suspend fun createSession(request: CreateSessionRequestDto): CreateSessionResponseDto {
        val response = httpClient.post("sessions") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return when(response.status) {
            HttpStatusCode.Created -> response.body()
            HttpStatusCode.Unauthorized -> throw AuthException.InvalidCredentials()
            else -> throw IllegalStateException("Login failed: HTTP ${response.status}")
        }
    }

    override suspend fun createAccessToken(request: CreateAccessTokenRequestDto): CreateAccessTokenResponseDto {
        val response = httpClient.post("access-tokens") {
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