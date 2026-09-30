package com.connectly.app.data

import kotlinx.coroutines.flow.Flow

data class AppUser(
    val uid: String,
    val username: String,
    val displayName: String,
    val bio: String = "",
    val avatarUrl: String? = null,
    val about: String = "",
    val services: String = ""
)

data class ChatPreview(val id: String, val name: String, val handle: String, val lastMessage: String, val time: String, val unread: Int, val online: Boolean)
data class ChatMessage(val id: String, val text: String, val sentByMe: Boolean, val time: String, val attachment: String? = null)

enum class RecoveryMethod { PHONE, CNIC, BACKUP_CODE }

enum class CallMode { AUDIO, VIDEO }

interface AuthRepository {
    suspend fun signInWithEmail(email: String, password: String): Result<AppUser>
    suspend fun signInWithGoogle(idToken: String): Result<AppUser>
    suspend fun createAccount(email: String, password: String, username: String): Result<AppUser>
    suspend fun restoreAccount(method: RecoveryMethod, value: String): Result<AppUser>
    fun observeCurrentUser(): Flow<AppUser?>
    suspend fun signOut()
}

interface MediaRepository {
    suspend fun uploadVoice(filePath: String): Result<String>
    suspend fun uploadImageOrVideo(filePath: String): Result<String>
}

interface RealtimeRepository {
    fun observeChats(uid: String): Flow<List<ChatPreview>>
    fun observeMessages(chatId: String): Flow<List<ChatMessage>>
    suspend fun sendMessage(chatId: String, message: ChatMessage): Result<Unit>
    suspend fun findUsers(query: String): Result<List<AppUser>>
}

class DemoAuthRepository : AuthRepository {
    private val demoUser = AppUser("demo", "@rayhan", "Rayhan Ahmed", "Building meaningful connections.")
    override suspend fun signInWithEmail(email: String, password: String) = Result.success(demoUser)
    override suspend fun signInWithGoogle(idToken: String) = Result.success(demoUser)
    override suspend fun createAccount(email: String, password: String, username: String) = Result.success(demoUser.copy(username = username))
    override suspend fun restoreAccount(method: RecoveryMethod, value: String) = Result.success(demoUser)
    override fun observeCurrentUser(): Flow<AppUser?> = kotlinx.coroutines.flow.flowOf(demoUser)
    override suspend fun signOut() = Unit
}

class DemoRealtimeRepository : RealtimeRepository {
    override fun observeChats(uid: String): Flow<List<ChatPreview>> = kotlinx.coroutines.flow.flowOf(
        listOf(
            ChatPreview("1", "Ayesha Khan", "@ayesha", "That sounds great!", "10:42", 2, true),
            ChatPreview("2", "Omar Farooq", "@omar", "Voice message · 0:18", "09:18", 0, false),
            ChatPreview("3", "Design Circle", "@designcircle", "Sara shared a video", "Yesterday", 0, true)
        )
    )
    override fun observeMessages(chatId: String): Flow<List<ChatMessage>> = kotlinx.coroutines.flow.flowOf(
        listOf(
            ChatMessage("1", "Hey! Are you free for a quick call?", false, "10:39"),
            ChatMessage("2", "Absolutely — give me two minutes.", true, "10:40"),
            ChatMessage("3", "That sounds great!", false, "10:42")
        )
    )
    override suspend fun sendMessage(chatId: String, message: ChatMessage) = Result.success(Unit)
    override suspend fun findUsers(query: String) = Result.success(listOf(AppUser("u2", "@ayesha", "Ayesha Khan", "Product designer"), AppUser("u3", "@omar", "Omar Farooq", "Creator & storyteller")))
}

class CloudinaryMediaRepository : MediaRepository {
    override suspend fun uploadVoice(filePath: String): Result<String> = Result.failure(IllegalStateException("Configure CLOUDINARY_CLOUD_NAME and CLOUDINARY_UPLOAD_PRESET"))
    override suspend fun uploadImageOrVideo(filePath: String): Result<String> = Result.failure(IllegalStateException("Configure CLOUDINARY_CLOUD_NAME and CLOUDINARY_UPLOAD_PRESET"))
}
