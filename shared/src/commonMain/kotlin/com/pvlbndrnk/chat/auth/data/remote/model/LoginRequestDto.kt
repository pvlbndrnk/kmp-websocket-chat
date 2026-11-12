package com.pvlbndrnk.chat.auth.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val username: String,
    val password: String
)
