package com.pvlbndrnk.chat.presentation.chat

import com.pvlbndrnk.chat.domain.model.Message

data class ChatState(
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
