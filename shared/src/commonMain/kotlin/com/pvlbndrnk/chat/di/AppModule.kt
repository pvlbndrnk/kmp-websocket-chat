package com.pvlbndrnk.chat.di

import com.pvlbndrnk.chat.AppViewModel
import com.pvlbndrnk.chat.auth.data.remote.model.CreateAccessTokenRequestDto
import com.pvlbndrnk.chat.auth.data.remote.model.CreateAccessTokenResponseDto
import com.pvlbndrnk.chat.auth.di.authModule
import com.pvlbndrnk.chat.data.remote.ChatSocketService
import com.pvlbndrnk.chat.data.remote.ChatSocketServiceImpl
import com.pvlbndrnk.chat.data.remote.MessageService
import com.pvlbndrnk.chat.data.remote.MessageServiceImpl
import com.pvlbndrnk.chat.presentation.chat.ChatViewModel
import com.pvlbndrnk.chat.session.SessionManager
import com.pvlbndrnk.chat.session.di.sessionModule
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.dsl.onClose

fun appModule(serverBaseUrl: String) = module {

    includes(authModule)
    includes(sessionModule)

    single {
        val host = Url(serverBaseUrl).host
        val sessionManager = get<SessionManager>()
        HttpClient {
            defaultRequest {
                url(serverBaseUrl)
            }
            install(Logging) {
                sanitizeHeader { header ->
                    header.equals(HttpHeaders.Authorization, ignoreCase = true)
                }
            }
            install(Auth) {
                bearer {
                    cacheTokens = false
                    loadTokens {
                        val accessToken = sessionManager.accessToken
                            ?: return@loadTokens null
                        val refreshToken = sessionManager.readRefreshToken()
                            ?: return@loadTokens null
                        BearerTokens(accessToken, refreshToken)
                    }
                    sendWithoutRequest { request ->
                        request.url.host == host &&
                                request.url.encodedPath in setOf("/messages", "/chat-socket")
                    }
                    refreshTokens {
                        val refreshToken = sessionManager.readRefreshToken()
                            ?: return@refreshTokens null
                        val response = client.post("access-tokens") {
                            markAsRefreshTokenRequest()
                            contentType(ContentType.Application.Json)
                            setBody(CreateAccessTokenRequestDto(refreshToken))
                        }
                        when (response.status) {
                            HttpStatusCode.Created -> {
                                val tokens = response.body<CreateAccessTokenResponseDto>()
                                sessionManager.setAccessToken(
                                    user = tokens.user.toUser(),
                                    accessToken = tokens.accessToken,
                                )
                                BearerTokens(tokens.accessToken, refreshToken)
                            }
                            HttpStatusCode.Unauthorized -> {
                                sessionManager.clearSession()
                                null
                            }
                            else -> error("Token refresh failed: HTTP ${response.status}")
                        }
                    }
                }
            }
            install(WebSockets)
            install(ContentNegotiation) {
                json()
            }
        }
    } onClose { it?.close() }

    single<MessageService> {
        MessageServiceImpl(get())
    }

    single<ChatSocketService> {
        val webSocketBaseUrl = when {
            serverBaseUrl.startsWith("https://") -> serverBaseUrl.replaceFirst("https://", "wss://")
            serverBaseUrl.startsWith("http://") -> serverBaseUrl.replaceFirst("http://", "ws://")
            else -> error("Unsupported server URL: $serverBaseUrl")
        }
        ChatSocketServiceImpl(get(), webSocketBaseUrl.trimEnd('/') + "/chat-socket")
    }

    viewModel { AppViewModel(get(), get()) }
    viewModel { ChatViewModel(get(), get()) }
}
