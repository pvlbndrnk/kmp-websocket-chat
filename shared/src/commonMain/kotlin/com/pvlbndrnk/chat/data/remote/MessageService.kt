package com.pvlbndrnk.chat.data.remote

import com.pvlbndrnk.chat.domain.model.Message

interface MessageService {
    suspend fun getAllMessages(): List<Message>
}