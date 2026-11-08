package com.pvlbndrnk.chat.auth.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateAccessTokenRequestDto(
    val refreshToken: String
)
