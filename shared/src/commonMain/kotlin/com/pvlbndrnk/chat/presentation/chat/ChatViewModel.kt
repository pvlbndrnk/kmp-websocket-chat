package com.pvlbndrnk.chat.presentation.chat

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pvlbndrnk.chat.data.remote.ChatSocketService
import com.pvlbndrnk.chat.data.remote.MessageService
import com.pvlbndrnk.chat.util.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ChatViewModel(
    private val messageService: MessageService,
    private val chatSocketService: ChatSocketService,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _messageText = mutableStateOf("")
    val messageText: State<String> = _messageText

    private val _chatState = mutableStateOf(ChatState())
    val chatState: State<ChatState> = _chatState

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    fun connectToChat() {
        getAllMessages()
        savedStateHandle.get<String>("username")?.let {
            viewModelScope.launch {
                val result = chatSocketService.initSession(it)
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

                    is Resource.Error -> {
                        _toastEvent.emit(result.message ?: "Unknown error")
                    }
                }
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
                isLoading = true
            )
            val result = messageService.getAllMessages()
            _chatState.value = chatState.value.copy(
                messages = result,
                isLoading = false
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
}