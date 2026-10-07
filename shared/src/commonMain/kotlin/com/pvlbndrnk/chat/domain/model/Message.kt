package com.pvlbndrnk.chat.domain.model

data class Message(
    val text: String,
    val userName: String,
    val formattedTime: String,
)
