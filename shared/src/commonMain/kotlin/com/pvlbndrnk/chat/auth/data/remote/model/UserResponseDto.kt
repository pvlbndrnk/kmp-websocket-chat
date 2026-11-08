package com.pvlbndrnk.chat.auth.data.remote.model

import com.pvlbndrnk.chat.core.user.model.User
import kotlinx.serialization.Serializable

@Serializable
data class UserResponseDto(
    val id: String,
    val username: String
) {
    fun toUser(): User = User(
        id = id,
        username = username
    )
}
