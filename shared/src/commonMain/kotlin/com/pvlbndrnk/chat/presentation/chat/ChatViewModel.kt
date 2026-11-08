package com.pvlbndrnk.chat.presentation.chat

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvlbndrnk.chat.data.remote.ChatSocketService
import com.pvlbndrnk.chat.data.remote.MessageService
import com.pvlbndrnk.chat.util.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ChatViewModel(
    private val messageService: MessageService,
    private val chatSocketService: ChatSocketService,
) : ViewModel() {

    private val _messageText = mutableStateOf("")
    val messageText: State<String> = _messageText

    private val _chatState = mutableStateOf(ChatState())
    val chatState: State<ChatState> = _chatState

    fun connectToChat() {
        getAllMessages()
        viewModelScope.launch {
            val result = chatSocketService.initSession()
            when (result) {
                is Resource.Success -> {
                    chatSocketService.observeMessages()
                        .onEach { message ->
                            val newList = chatState.value.messages.toMutableList().apply {
                                add(0, message)
                            }
                            _chatState.value = chatState.value.copy(
                                messages = newList
                            )
                        }.launchIn(viewModelScope)
                }
                is Resource.Error -> {}
            }
        }
    }

    fun onMessageChange(message: String) {
        _messageText.value = message
    }

    fun disconnect() {
        viewModelScope.launch {
            chatSocketService.closeSession()
        }
    }

    fun sendMessage() {
        viewModelScope.launch {
            if (messageText.value.isNotBlank()) {
                chatSocketService.sendMessage(messageText.value)
            }
        }
    }

    fun getAllMessages() {
        viewModelScope.launch {
            _chatState.value = chatState.value.copy(
                isLoading = true,
                errorMessage = null,
            )
            try {
                val result = messageService.getAllMessages()
                _chatState.value = chatState.value.copy(
                    messages = result,
                    isLoading = false,
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _chatState.value = chatState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Couldn't load messages",
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
}
