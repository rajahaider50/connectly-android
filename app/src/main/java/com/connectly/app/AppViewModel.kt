package com.connectly.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.connectly.app.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AppRoute { data object Home : AppRoute; data object Search : AppRoute; data object Profile : AppRoute; data class Chat(val id: String, val name: String) : AppRoute; data object Recovery : AppRoute }

class AppViewModel : ViewModel() {
    private val auth: AuthRepository = DemoAuthRepository()
    private val realtime: RealtimeRepository = DemoRealtimeRepository()
    private val _route = MutableStateFlow<AppRoute>(AppRoute.Home)
    val route: StateFlow<AppRoute> = _route.asStateFlow()
    private val _chats = MutableStateFlow<List<ChatPreview>>(emptyList())
    val chats: StateFlow<List<ChatPreview>> = _chats.asStateFlow()
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()
    private val _searchResults = MutableStateFlow<List<AppUser>>(emptyList())
    val searchResults: StateFlow<List<AppUser>> = _searchResults.asStateFlow()
    val currentUser = MutableStateFlow(AppUser("demo", "@rayhan", "Rayhan Ahmed", "Building meaningful connections."))

    init { viewModelScope.launch { realtime.observeChats("demo").collect { _chats.value = it } } }
    fun go(route: AppRoute) { _route.value = route; if (route is AppRoute.Chat) viewModelScope.launch { realtime.observeMessages(route.id).collect { _messages.value = it } } }
    fun search(query: String) { viewModelScope.launch { _searchResults.value = if (query.length > 1) realtime.findUsers(query).getOrDefault(emptyList()) else emptyList() } }
    fun send(chatId: String, text: String) { if (text.isBlank()) return; val message = ChatMessage("local-${System.currentTimeMillis()}", text.trim(), true, "now"); _messages.value = _messages.value + message; viewModelScope.launch { realtime.sendMessage(chatId, message) } }
    fun restore(method: RecoveryMethod, value: String, onDone: () -> Unit) { viewModelScope.launch { auth.restoreAccount(method, value).onSuccess { currentUser.value = it; onDone() } } }
}
