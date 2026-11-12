package com.pvlbndrnk.chat.auth.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class UserRequestDto(
    val username: String,
    val password: String
)
