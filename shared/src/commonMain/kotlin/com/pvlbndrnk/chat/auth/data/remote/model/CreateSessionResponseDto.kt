package com.pvlbndrnk.chat.auth.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateSessionResponseDto(
    val user: UserResponseDto,
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresInSeconds: Long
)
