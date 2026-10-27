package com.pvlbndrnk.data

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.pvlbndrnk.data.model.Message
import kotlinx.coroutines.flow.toList

class MessageDataSourceImpl(
    database: MongoDatabase
): MessageDataSource {
    private val messages = database.getCollection<Message>(
        collectionName = "messages"
    )

    override suspend fun insertMessage(message: Message) {
        messages.insertOne(message)
    }

    override suspend fun getAllMessages(): List<Message> =
        messages.find().toList().sortedByDescending(Message::timestamp).toList()
}