package com.pvlbndrnk.chat.data.remote

import com.pvlbndrnk.chat.domain.model.Message

interface MessageService {

    suspend fun getAllMessages(): List<Message>

    companion object {
        const val BASE_URL = "https://pvlbndrnk.duckdns.org"
    }

    sealed class Endpoints(val url: String) {
        object GetAllMessages: Endpoints("$BASE_URL/messages")
    }
}