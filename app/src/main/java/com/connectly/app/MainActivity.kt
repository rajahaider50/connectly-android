package com.connectly.app

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.connectly.app.data.*
import com.connectly.app.ui.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); setContent { ConnectlyTheme { ConnectlyApp() } } }
}

@Composable fun ConnectlyApp(vm: AppViewModel = viewModel()) {
    val route by vm.route.collectAsState()
    Surface(modifier = Modifier.fillMaxSize(), color = Ink) {
        when (val current = route) {
            AppRoute.Home -> HomeScreen(vm)
            AppRoute.Search -> SearchScreen(vm)
            AppRoute.Profile -> ProfileScreen(vm)
            AppRoute.Recovery -> RecoveryScreen(vm)
            is AppRoute.Chat -> ChatScreen(vm, current)
        }
    }
}

@Composable private fun AppHeader(title: String, subtitle: String? = null, onProfile: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) { Text(title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold); subtitle?.let { Text(it, color = TextMuted, style = MaterialTheme.typography.bodyMedium) } }
        IconButton(onClick = onProfile) { Box(Modifier.size(42.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Violet, Cyan))), contentAlignment = Alignment.Center) { Text("RA", fontWeight = FontWeight.Bold, color = Color.White) } }
    }
}

@Composable private fun BottomNav(selected: AppRoute, go: (AppRoute) -> Unit) {
    NavigationBar(containerColor = Surface) {
        NavigationBarItem(selected is AppRoute.Home, onClick = { go(AppRoute.Home) }, icon = { Icon(Icons.Default.ChatBubble, null) }, label = { Text("Chats") })
        NavigationBarItem(selected is AppRoute.Search, onClick = { go(AppRoute.Search) }, icon = { Icon(Icons.Default.Search, null) }, label = { Text("Discover") })
        NavigationBarItem(selected is AppRoute.Profile, onClick = { go(AppRoute.Profile) }, icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profile") })
    }
}

@Composable private fun HomeScreen(vm: AppViewModel) {
    val chats by vm.chats.collectAsState()
    Scaffold(containerColor = Ink, bottomBar = { BottomNav(AppRoute.Home, vm::go) }) { pad ->
        Column(Modifier.padding(pad)) {
            AppHeader("Good evening, Rayhan", "Stay close to your people") { vm.go(AppRoute.Profile) }
            Row(Modifier.padding(horizontal = 20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilledTonalButton(onClick = { vm.go(AppRoute.Search) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) { Icon(Icons.Default.PersonAdd, null); Spacer(Modifier.width(8.dp)); Text("New chat") }
                OutlinedButton(onClick = { vm.go(AppRoute.Search) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(14.dp)) { Icon(Icons.Default.VideoCall, null); Spacer(Modifier.width(8.dp)); Text("Meet now") }
            }
            Text("Your conversations", Modifier.padding(20.dp, 26.dp, 20.dp, 10.dp), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            LazyColumn(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { items(chats) { chat -> ChatRow(chat) { vm.go(AppRoute.Chat(chat.id, chat.name)) } } }
        }
    }
}

@Composable private fun ChatRow(chat: ChatPreview, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Surface).clickable(onClick = onClick).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(52.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Violet, Cyan))), contentAlignment = Alignment.Center) { Text(chat.name.split(" ").map { it.first() }.take(2).joinToString(""), fontWeight = FontWeight.Bold) }
        Column(Modifier.weight(1f).padding(horizontal = 13.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Text(chat.name, fontWeight = FontWeight.SemiBold); if (chat.online) { Spacer(Modifier.width(6.dp)); Box(Modifier.size(7.dp).clip(CircleShape).background(Color(0xFF34D399))) } }; Text(chat.lastMessage, color = TextMuted, maxLines = 1) }
        Column(horizontalAlignment = Alignment.End) { Text(chat.time, color = TextMuted, style = MaterialTheme.typography.labelSmall); if (chat.unread > 0) { Spacer(Modifier.height(6.dp)); Badge { Text(chat.unread.toString()) } } }
    }
}

@Composable private fun SearchScreen(vm: AppViewModel) {
    var query by remember { mutableStateOf("") }; val results by vm.searchResults.collectAsState()
    Scaffold(containerColor = Ink, bottomBar = { BottomNav(AppRoute.Search, vm::go) }) { pad -> Column(Modifier.padding(pad)) {
        AppHeader("Discover people", "Find anyone by their @username") { vm.go(AppRoute.Profile) }
        OutlinedTextField(query, { query = it; vm.search(it) }, Modifier.fillMaxWidth().padding(horizontal = 20.dp), placeholder = { Text("@username") }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true, shape = RoundedCornerShape(16.dp))
        LazyColumn(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { items(results) { user -> UserResult(user) { vm.go(AppRoute.Chat(user.uid, user.displayName)) } } }
    } }
}

@Composable private fun UserResult(user: AppUser, onChat: () -> Unit) { Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Surface).padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(48.dp).clip(CircleShape).background(Violet), contentAlignment = Alignment.Center) { Text(user.displayName.take(1)) }; Column(Modifier.weight(1f).padding(horizontal = 12.dp)) { Text(user.displayName, fontWeight = FontWeight.SemiBold); Text(user.username, color = TextMuted); Text(user.bio, color = TextMuted, style = MaterialTheme.typography.labelSmall) }; IconButton(onClick = onChat) { Icon(Icons.Default.Chat, null, tint = Cyan) } } }

@Composable private fun ProfileScreen(vm: AppViewModel) { val user by vm.currentUser.collectAsState(); Scaffold(containerColor = Ink) { pad -> Column(Modifier.padding(pad).padding(20.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = { vm.go(AppRoute.Home) }) { Icon(Icons.Default.ArrowBack, null) }; Text("Your profile", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }; Spacer(Modifier.height(22.dp)); Box(Modifier.size(88.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Violet, Cyan))), contentAlignment = Alignment.Center) { Text("RA", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }; Spacer(Modifier.height(14.dp)); Text(user.displayName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text(user.username, color = Cyan); Spacer(Modifier.height(8.dp)); Text(user.bio, color = TextMuted); Spacer(Modifier.height(26.dp)); ProfileItem(Icons.Default.Edit, "Edit profile", "Photo, bio and services") {}; ProfileItem(Icons.Default.Security, "Account recovery", "Set phone, CNIC or backup code") { vm.go(AppRoute.Recovery) }; ProfileItem(Icons.Default.Notifications, "Notifications", "Push alerts and call reminders") {}; ProfileItem(Icons.Default.Logout, "Sign out", "End this session") {} } } }

@Composable private fun ProfileItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, detail: String, onClick: () -> Unit) { Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable(onClick = onClick).padding(vertical = 15.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = Violet, modifier = Modifier.size(23.dp)); Column(Modifier.padding(start = 14.dp)) { Text(title, fontWeight = FontWeight.SemiBold); Text(detail, color = TextMuted, style = MaterialTheme.typography.bodySmall) }; Spacer(Modifier.weight(1f)); Icon(Icons.Default.ChevronRight, null, tint = TextMuted) } }

@Composable private fun RecoveryScreen(vm: AppViewModel) { var method by remember { mutableStateOf(RecoveryMethod.PHONE) }; var value by remember { mutableStateOf("") }; Scaffold(containerColor = Ink) { pad -> Column(Modifier.padding(pad).padding(20.dp)) { IconButton(onClick = { vm.go(AppRoute.Profile) }) { Icon(Icons.Default.ArrowBack, null) }; Text("Restore your account", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold); Text("Choose one verified recovery method.", color = TextMuted); Spacer(Modifier.height(24.dp)); RecoveryMethod.values().forEach { item -> FilterChip(selected = method == item, onClick = { method = item }, label = { Text(item.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }) }, modifier = Modifier.padding(end = 8.dp)) }; Spacer(Modifier.height(18.dp)); OutlinedTextField(value, { value = it }, Modifier.fillMaxWidth(), label = { Text(when(method) { RecoveryMethod.PHONE -> "Phone number"; RecoveryMethod.CNIC -> "13-digit CNIC number"; RecoveryMethod.BACKUP_CODE -> "8-digit backup code" }) }, singleLine = true, shape = RoundedCornerShape(16.dp)); Spacer(Modifier.height(18.dp)); Button(onClick = { vm.restore(method, value) { vm.go(AppRoute.Home) } }, Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) { Text("Verify & restore") } } } }

@Composable private fun ChatScreen(vm: AppViewModel, route: AppRoute.Chat) { var text by remember { mutableStateOf("") }; val messages by vm.messages.collectAsState(); val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {} ; Scaffold(containerColor = Ink, topBar = { TopAppBar(title = { Column { Text(route.name); Text("Secure conversation", style = MaterialTheme.typography.labelSmall, color = TextMuted) } }, navigationIcon = { IconButton(onClick = { vm.go(AppRoute.Home) }) { Icon(Icons.Default.ArrowBack, null) } }, actions = { IconButton(onClick = { permissionLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO)) }) { Icon(Icons.Default.Call, null) }; IconButton(onClick = { permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)) }) { Icon(Icons.Default.VideoCall, null) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Ink)) }, bottomBar = { Row(Modifier.fillMaxWidth().background(Surface).padding(10.dp), verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = { permissionLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO)) }) { Icon(Icons.Default.Mic, null, tint = Cyan) }; OutlinedTextField(text, { text = it }, Modifier.weight(1f), placeholder = { Text("Write a message") }, shape = RoundedCornerShape(18.dp), singleLine = true); IconButton(onClick = { vm.send(route.id, text); text = "" }) { Icon(Icons.Default.Send, null, tint = Violet) } } }) { pad -> LazyColumn(Modifier.padding(pad).padding(horizontal = 16.dp), contentPadding = PaddingValues(vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { items(messages) { m -> Row(Modifier.fillMaxWidth(), horizontalArrangement = if (m.sentByMe) Arrangement.End else Arrangement.Start) { Surface(color = if (m.sentByMe) Violet else SurfaceElevated, shape = RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp)) { Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) { Text(m.text); Text(m.time, style = MaterialTheme.typography.labelSmall, color = if (m.sentByMe) Color.White.copy(.7f) else TextMuted) } } } } } } }
