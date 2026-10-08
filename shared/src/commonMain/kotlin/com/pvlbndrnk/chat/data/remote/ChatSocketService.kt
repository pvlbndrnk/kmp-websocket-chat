package com.pvlbndrnk.chat.data.remote

import com.pvlbndrnk.chat.domain.model.Message
import com.pvlbndrnk.chat.util.Resource
import kotlinx.coroutines.flow.Flow

interface ChatSocketService {

    suspend fun initSession(username: String) : Resource<Unit>
    suspend fun closeSession()

    fun observeMessages(): Flow<Message>
    suspend fun sendMessage(message: String)

    companion object {
        const val BASE_URL = "ws://10.0.2.2:8080"
    }

    sealed class Endpoints(val url: String) {
        object ChatSocket: Endpoints("$BASE_URL/chat-socket")
    }
}