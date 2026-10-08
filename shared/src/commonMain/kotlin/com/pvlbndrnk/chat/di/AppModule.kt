package com.pvlbndrnk.chat.di

import com.pvlbndrnk.chat.data.remote.ChatSocketServiceImpl
import com.pvlbndrnk.chat.data.remote.ChatSocketService
import com.pvlbndrnk.chat.data.remote.MessageService
import com.pvlbndrnk.chat.data.remote.MessageServiceImpl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import org.koin.dsl.module
import org.koin.dsl.onClose

val appModule = module {
    single {
        HttpClient {
            install(Logging)
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
        ChatSocketServiceImpl(get())
    }
}
