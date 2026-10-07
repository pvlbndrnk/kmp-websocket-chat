package com.pvlbndrnk.chat.data.remote.dto

import com.pvlbndrnk.chat.domain.model.Message
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class MessageDto(
    val id: String,
    val text: String,
    val userName: String,
    val timestamp: Long,
) {
    fun toMessage(): Message {
        val formattedDate = Instant.fromEpochMilliseconds(timestamp)
            .toString()
            .substringBefore('T')
        return Message(
            text = text,
            userName = userName,
            formattedTime = formattedDate,
        )
    }
}
