package com.pvlbndrnk.chat.auth.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class TokenRequestDto(
    val refreshToken: String
)
