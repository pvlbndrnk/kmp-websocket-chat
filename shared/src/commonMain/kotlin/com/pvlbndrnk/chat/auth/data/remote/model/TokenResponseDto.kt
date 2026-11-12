package com.pvlbndrnk.chat.auth.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponseDto(
    val user: UserResponseDto,
    val token: String,
    val expiresIn: Long
)
