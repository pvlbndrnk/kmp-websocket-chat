package com.pvlbndrnk.chat.auth.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateAccessTokenResponseDto(
    val user: UserResponseDto,
    val accessToken: String,
    val accessTokenExpiresInSeconds: Long
)
