package com.pvlbndrnk.data

import com.pvlbndrnk.data.model.Message

interface MessageDataSource {
    suspend fun insertMessage(message: Message)
    suspend fun getAllMessages(): List<Message>
}