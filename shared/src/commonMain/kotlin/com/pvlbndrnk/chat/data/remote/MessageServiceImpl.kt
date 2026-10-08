package com.pvlbndrnk.chat.data.remote

import com.pvlbndrnk.chat.domain.model.Message
import com.pvlbndrnk.chat.data.remote.dto.MessageDto
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

class MessageServiceImpl(
    private val client: HttpClient,
) : MessageService {
    override suspend fun getAllMessages(): List<Message> {
        val response = client.get(MessageService.Endpoints.GetAllMessages.url)
        check(response.status.value in 200..299) {
            "Failed to load messages: HTTP ${response.status.value}"
        }
        val messages = Json.decodeFromString<List<MessageDto>>(response.bodyAsText())
        return messages.map(MessageDto::toMessage)
    }
}
