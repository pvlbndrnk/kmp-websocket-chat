package com.pvlbndrnk.chat.data.remote

import com.pvlbndrnk.chat.data.remote.dto.MessageDto
import com.pvlbndrnk.chat.domain.model.Message
import com.pvlbndrnk.chat.util.Resource
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.http.takeFrom
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json

class ChatSocketServiceImpl(
    private val client: HttpClient
) : ChatSocketService {

    private var socket: WebSocketSession? = null

    override suspend fun initSession(username: String): Resource<Unit> = try {
        socket = client.webSocketSession {
            url.takeFrom("${ChatSocketService.Endpoints.ChatSocket.url}?username=$username")
        }
        if (socket?.isActive == true) {
            Resource.Success(Unit)
        } else {
            Resource.Error(message = "Couldn't establish a connection.")
        }
    } catch (e: Exception) {
        Resource.Error(message = e.message ?: "Unknown error")
    }


    override suspend fun closeSession() {
        socket?.close()
    }

    override fun observeMessages(): Flow<Message> = try {
        socket
            ?.incoming
            ?.receiveAsFlow()
            ?.filter { it is Frame.Text }
            ?.map {
                val json = (it as? Frame.Text)?.readText() ?: ""
                val messageDto = Json.decodeFromString<MessageDto>(json)
                messageDto.toMessage()
            } ?: flow { }
    } catch (e: Exception) {
        e.printStackTrace()
        flow {}
    }

    override suspend fun sendMessage(message: String) {
        try {
            socket?.send(Frame.Text(message))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
