package com.pvlbndrnk.room

import com.pvlbndrnk.data.MessageDataSource
import com.pvlbndrnk.data.model.Message
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class RoomController(
    private val messageDataSource: MessageDataSource
) {
    private val members = ConcurrentHashMap<String, Member>()

    fun onJoin(username: String, sessionId: String, socket: WebSocketSession) {
        if (members.contains(username)) {
            throw MemberAlreadyExistsException()
        }
        members[username] = Member(username, sessionId, socket)
    }

    suspend fun tryDisconnect(username: String) {
        members[username]?.socket?.close()
        if (members.containsKey(username)) {
            members.remove(username)
        }
    }

    suspend fun sendMessage(senderUsername: String, message: String) {
        members.values.forEach { member ->
            val messageEntity = Message(
                text = message,
                userName = senderUsername,
                timestamp = System.currentTimeMillis(),
            )
            messageDataSource.insertMessage(messageEntity)

            val encodedMessage = Json.encodeToString(messageEntity)
            member.socket.send(Frame.Text(encodedMessage))
        }
    }

    suspend fun getAllMessages(): List<Message> =
        messageDataSource.getAllMessages()
}