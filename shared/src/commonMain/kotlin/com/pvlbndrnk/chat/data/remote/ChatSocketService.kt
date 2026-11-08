package com.pvlbndrnk.chat.data.remote

import com.pvlbndrnk.chat.domain.model.Message
import com.pvlbndrnk.chat.util.Resource
import kotlinx.coroutines.flow.Flow

interface ChatSocketService {

    suspend fun initSession() : Resource<Unit>
    suspend fun closeSession()

    fun observeMessages(): Flow<Message>
    suspend fun sendMessage(message: String)

}
