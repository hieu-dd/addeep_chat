package net.itanchi.addeep.core.data

import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import net.itanchi.addeep.core.data.api.*
import net.itanchi.addeep.core.data.api.ConversationApi
import net.itanchi.addeep.core.data.api.EventApi
import net.itanchi.addeep.core.data.api.MediaApi
import net.itanchi.addeep.core.data.api.PointApi
import net.itanchi.addeep.core.data.api.UsersApi
import net.itanchi.addeep.core.data.api.request.*
import net.itanchi.addeep.core.data.model.*
import net.itanchi.addeep.core.data.repository.AuthRepo
import net.itanchi.addeep.core.data.repository.ConversationRepo
import net.itanchi.addeep.core.data.repository.UserRepo
import net.itanchi.addeep.core.util.ControlledRunner
import net.itanchi.addeep.core.util.DataState
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppDataManager : KoinComponent {
    private val authRepo: AuthRepo by inject()
    private val usersAPI: UsersApi by inject()
    private val userRepo: UserRepo by inject()
    private val conversationApi: ConversationApi by inject()
    private val conversationRepo: ConversationRepo by inject()
    private val pointApi: PointApi by inject()
    private val eventApi: EventApi by inject()
    private val mediaApi: MediaApi by inject()

    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    private val avatarRunner = ControlledRunner<Unit>()

    var latestConversationId: Long = 0

    fun onDestroy() {
        scope.coroutineContext.cancel()
    }

    // Authentication

    /**
     * Logout
     */
    suspend fun logout(): Flow<DataState<Boolean>> = flow {
        emit(DataState(loading = true))
        try {
            // TODO: Remove all data
            conversationApi.stopStreamingConversation()
            authRepo.delete()
            conversationRepo.deleteAll()
            conversationRepo.deleteAllMessages()
            userRepo.deleteAllParticipants()
            userRepo.deleteAll()
            emit(DataState(data = true))
        } catch (error: Throwable) {
            Logger.e(error) {
                "logout"
            }
            emit(DataState(exception = error))
        }
    }

    // User

    /**
     * Get user
     */
    suspend fun getUser(
        onlyLocalData: Boolean,
        onlyRemoteData: Boolean,
        addeepId: String? = null,
        phone: String? = null,
    ): Flow<DataState<User>> = flow {
        emit(DataState(loading = true))
        try {
            requireAuthentication()

            if (!onlyRemoteData) {
                emit(DataState(data = userRepo.findMe()))
            }

            if (!onlyLocalData) {
                val userResponse = usersAPI.getUser(GetUserRequest(addeepId, phone))
                if (userResponse.isSuccessful()) {
                    userResponse.data?.let {
                        userRepo.upsert(it.apply { isMe = true })
                    }
                    emit(DataState(data = userResponse.data))
                } else {
                    emit(DataState(exception = Throwable(message = userResponse.message)))
                }
            }
        } catch (error: Throwable) {
            Logger.e(error) {
                "getUser"
            }
            emit(DataState(exception = error))
        }
    }

    /**
     * Update user
     */
    suspend fun updateUser(
        name: String? = null,
        email: String? = null,
        dob: LocalDate? = null,
        gender: Gender? = null,
        preferences: Preferences? = null,
        addeepId: String? = null,
        allowToSearchByAddeepId: Boolean? = null,
    ): Flow<DataState<User>> = flow {
        emit(DataState(loading = true))
        try {
            requireAuthentication()
            val updateUserResponse = usersAPI.updateUser(
                UpdateUserRequest(
                    name = name,
                    email = email,
                    dob = dob,
                    gender = gender,
                    preferences = preferences,
                    addeepId = addeepId,
                    allowToSearchByAddeepId = allowToSearchByAddeepId
                )
            )
            if (updateUserResponse.isSuccessful()) {
                updateUserResponse.data?.let {
                    userRepo.upsert(it.apply { isMe = true })
                }
                emit(DataState(data = updateUserResponse.data))
            } else {
                emit(DataState(exception = Throwable(message = updateUserResponse.message)))
            }
        } catch (error: Throwable) {
            Logger.e(error) {
                "updateUser"
            }
            emit(DataState(exception = error))
        }
    }

    /**
     * Upload user avatar
     */
    suspend fun uploadUserAvatar(
        avatar: ByteArray
    ): Flow<DataState<Unit>> = flow {
        emit(DataState(loading = true))
        try {
            requireAuthentication()
            avatarRunner.cancelPreviousThenRun {
                usersAPI.uploadUserAvatar(
                    UploadRequest(
                        name = "avatar.png",
                        contentType = "image/png",
                        contents = avatar
                    )
                ).collect {
                    Logger.d("Uploading avatar: ${it.first} - ${it.second}")
                }
            }
            emit(DataState(data = Unit))
        } catch (error: Throwable) {
            Logger.e(error) {
                "uploadUserAvatar"
            }
            emit(DataState(exception = error))
        }
    }

    /**
     * Update auth token
     */
    suspend fun updateAuthToken(
        token: String,
    ) {
        try {
            authRepo.saveAuthToken(token)
            authRepo.getPushToken()?.let { usersAPI.updatePushToken(UpdatePushTokenRequest(it)) }
        } catch (error: Throwable) {
            Logger.e(error) {
                "updateAuthToken"
            }
        }
    }

    /**
     * Update push token
     */
    suspend fun updatePushToken(
        token: String,
    ) {
        scope.launch {
            try {
                authRepo.savePushToken(token)
                requireAuthentication()
                usersAPI.updatePushToken(UpdatePushTokenRequest(token))
            } catch (error: Throwable) {
                Logger.e(error) {
                    "updatePushToken"
                }
            }
        }
    }

    /**
     * Sync contacts
     */
    suspend fun syncUserContacts(
        localContacts: List<Contact>,
    ): Flow<DataState<List<User>>> = flow {
        emit(DataState(loading = true))
        try {
            requireAuthentication()

            val contactsResponse = usersAPI.syncUserContacts(SyncUserContactsRequest(localContacts))
            if (contactsResponse.isSuccessful()) {
                emit(DataState(data = contactsResponse.data))
            } else {
                emit(DataState(exception = Throwable(message = contactsResponse.message)))
            }
        } catch (error: Throwable) {
            Logger.e(error) {
                "synContacts"
            }
            emit(DataState(exception = error))
        }
    }

    /**
     * Add contact
     */
    suspend fun addContact(userId: Long): Flow<DataState<Unit>> = flow {
        emit(DataState(loading = true))
        try {
            with(usersAPI.addContact(AddContactRequest(userId))) {
                if (isSuccessful()) {
                    emit(DataState(data = Unit))
                } else {
                    emit(DataState(exception = Throwable(message = message)))
                }
            }
        } catch (error: Throwable) {
            Logger.e(error) {
                "add Contact"
            }
            emit(DataState(exception = error))
        }
    }


    // Event

    /**
     * Get events
     */
    suspend fun getEvents(): Flow<DataState<List<Event>>> = flow {
        emit(DataState(loading = true))
        try {
            requireAuthentication()

            val eventsResponse = eventApi.getEvents(GetEventsRequest())
            if (eventsResponse.isSuccessful()) {
                emit(DataState(data = eventsResponse.data))
            } else {
                emit(DataState(exception = Throwable(message = eventsResponse.message)))
            }
        } catch (error: Throwable) {
            Logger.e(error) {
                "getEvents"
            }
            emit(DataState(exception = error))
        }
    }

    // Points

    /**
     * Get point history
     */
    suspend fun getPointsHistory(
        page: Int,
        pageSize: Int,
    ): Flow<DataState<List<Point>>> = flow {
        emit(DataState(loading = true))
        try {
            requireAuthentication()

            val pointsResponse = pointApi.getPointsHistory(GetPointsHistoryRequest(page, pageSize))
            if (pointsResponse.isSuccessful()) {
                emit(DataState(data = pointsResponse.data))
            } else {
                emit(DataState(exception = Throwable(message = pointsResponse.message)))
            }
        } catch (error: Throwable) {
            Logger.e(error) {
                "getPoints"
            }
            emit(DataState(exception = error))
        }
    }

    // Conversation

    /**
     * Get all conversations
     */
    suspend fun getConversations(): Flow<DataState<List<Conversation>>> = flow {
        emit(DataState(loading = true))
        try {
            val token = requireAuthentication()

            scope.launch {
                // fetch remote data
                conversationApi.getConversations().data?.forEach {
                    // update to local db
                    updateLocalConversation(
                        conversation = it,
                        saveParticipants = it.type == ConversationType.Single,
                    )
                }
                conversationApi.streamConversations(token = token)
                    .catch { error ->
                        Logger.e(error) {
                            "streamConversations"
                        }
                    }
                    .collect {
                        updateLocalConversation(
                            conversation = it,
                            saveParticipants = it.type == ConversationType.Single,
                            saveMyMessage = false,
                        )
                    }
            }
            // listen and emit local data on changed
            getLocalConversations().collect {
                emit(DataState(data = it.sortedByDescending { it.messages.maxByOrNull { it.createdAt }?.createdAt }))
            }
        } catch (error: Throwable) {
            Logger.e(error) {
                "getAllConversations"
            }
            emit(DataState(data = listOf()))
        }
    }

    /**
     * Get conversation with user
     */
    suspend fun findConversationWithUser(userId: Long): Conversation? {
        return conversationRepo.findSingleConversationWithUser(userId)
    }

    /**
     * Get conversation by id
     */
    suspend fun getConversation(
        conversationId: Long,
        onlyLocalData: Boolean,
    ): Flow<DataState<Conversation>> = flow {
        emit(DataState(loading = true))
        try {
            requireAuthentication()

            if (!onlyLocalData) {
                scope.launch {
                    // fetch remote data
                    conversationApi.getConversation(GetConversationRequest(conversationId)).data?.let {
                        // update to local db
                        updateLocalConversation(it, saveCreator = false)
                    }
                }
            }
            // listen and emit local data on changed
            getLocalConversation(conversationId).collect {
                emit(DataState(data = it))
            }
        } catch (error: Throwable) {
            Logger.e(error) {
                "getConversation"
            }
        }
    }

    /**
     * Get conversation messages
     */
    suspend fun getConversationMessages(
        conversationId: Long,
        messageBefore: Long? = null,
        messageAfter: Long? = null,
    ): List<Message> {
        try {
            requireAuthentication()

            val response = conversationApi.getConversationMessages(
                GetConversationMessagesRequest(conversationId, messageBefore, messageAfter)
            )
            return if (response.isSuccessful()) {
                val messages = (response.data ?: listOf()).map { it.copy(status = MessageStatus.Sent) }
                conversationRepo.upsertConversationMessages(conversationId, messages)
                messages
            } else {
                listOf()
            }
        } catch (error: Throwable) {
            Logger.e(error) {
                "getConversationMessages"
            }
            return listOf()
        }
    }

    suspend fun getGifs(
        search: String,
        page: Int,
        pageSize: Int,
    ): Flow<DataState<List<Gif>>> = flow {
        emit(DataState(loading = true))

        try {
            requireAuthentication()

            val response = mediaApi.getGifs(
                GifRequest(search, page, pageSize)
            )

            if (response.isSuccessful()) {
                emit(DataState(data = response.data))
            } else {
                emit(DataState(exception = Throwable(message = response.message)))
            }

        } catch (error: Throwable) {
            Logger.e(error) {
                "getGifListAPI"
            }
            emit(DataState(exception = error))
        }
    }

    /**
     * Send conversation message
     */
    suspend fun sendConversationMessage(
        conversationId: Long,
        userId: Long,
        message: Message,
    ): Long {
        try {
            requireAuthentication()

            val sentConversationId = if (conversationId == 0L) createConversationWithUser(userId) else conversationId
            if (sentConversationId != 0L) {
                val sendingMessage = message.copy(
                    sender = userRepo.findMe()!!,
                    status = MessageStatus.Sending,
                )
                conversationRepo.upsertConversationMessages(sentConversationId, listOf(sendingMessage))
                val response = conversationApi.sendConversationMessage(
                    SendConversationMessageRequest(
                        conversationId = sentConversationId,
                        message = message,
                    )
                )
                val sentMessage = if (response.isSuccessful() && response.data != null) {
                    response.data.copy(
                        localId = sendingMessage.localId,
                        status = MessageStatus.Sent,
                    )
                } else {
                    sendingMessage.copy(
                        status = MessageStatus.Failed,
                    )
                }
                conversationRepo.upsertConversationMessages(sentConversationId, listOf(sentMessage))
            }
            return sentConversationId
        } catch (error: Throwable) {
            Logger.e(error) {
                "sendConversationMessage"
            }
            return 0L
        }
    }

    /**
     * View conversation message
     */
    suspend fun viewConversationMessage(
        conversationId: Long,
        message: Message,
    ): List<Event> {
        return try {
            val token = requireAuthentication()
            conversationApi.triggerConversationMessage(
                token = token,
                request = TriggerConversationMessageRequest(
                    conversationId = conversationId,
                    messageId = message.id,
                    action = ActionType.View,
                )
            )
        } catch (error: Throwable) {
            Logger.e(error) {
                "viewConversationMessage"
            }
            listOf()
        }
    }

    private suspend fun getLocalConversations(): Flow<List<Conversation>> {
        return conversationRepo.findAll().map { localConversations ->
            localConversations.mapNotNull { localConversation ->
                val messages = conversationRepo.findConversationMessages(localConversation.id, limit = 1)
                    .firstOrNull() ?: listOf()
                if (messages.isNotEmpty())
                    Conversation(
                        id = localConversation.id,
                        title = localConversation.title,
                        type = localConversation.type,
                        creator = userRepo.findById(localConversation.creator.id) ?: localConversation.creator,
                        participants = userRepo.findConversationParticipants(localConversation.id, limit = 4),
                        messages = messages.map {
                            it.copy(
                                sender = userRepo.findById(it.sender.id) ?: it.sender
                            )
                        },
                        createdAt = localConversation.createdAt,
                        updatedAt = localConversation.updatedAt,
                    )
                else null
            }
        }
    }

    private suspend fun getLocalConversation(conversationId: Long): Flow<Conversation> {
        return conversationRepo.findById(conversationId)
            .combine(conversationRepo.findConversationMessages(conversationId)) { conversation, messages ->
                conversation.copy(
                    creator = userRepo.findById(conversation.creator.id) ?: conversation.creator,
                    participants = userRepo.findConversationParticipants(conversation.id),
                    messages = messages.map { message ->
                        message.copy(sender = userRepo.findById(message.sender.id) ?: message.sender)
                    }
                )
            }
    }

    private suspend fun createConversationWithUser(userId: Long): Long {
        return conversationApi.createConversation(
            CreateConversationRequest(
                title = "",
                type = ConversationType.Single,
                participantIds = listOf(userId)
            )
        ).data?.also {
            updateLocalConversation(
                conversation = it,
                saveCreator = false,
                saveMessage = false,
                saveMyMessage = false,
                saveSender = false,
            )
        }?.id ?: 0L
    }

    private suspend fun updateLocalConversation(
        conversation: Conversation,
        saveCreator: Boolean = true,
        saveParticipants: Boolean = true,
        saveMessage: Boolean = true,
        saveMyMessage: Boolean = true,
        saveSender: Boolean = true,
    ) {
        val me = userRepo.findMe()!!
        conversationRepo.upsert(conversation)
        if (saveCreator) userRepo.upsert(conversation.creator)
        if (saveParticipants) userRepo.upsertConversationParticipants(conversation.id, conversation.participants)
        if (saveMessage) conversationRepo.upsertConversationMessages(
            conversation.id,
            conversation.messages
                .filter { if (saveMyMessage) true else it.sender.id != me.id }
                .map { it.copy(status = MessageStatus.Sent) }
        )
        if (saveSender) conversation.messages.map { it.sender }.distinctBy { it.id }.forEach { userRepo.upsert(it) }
    }

    private suspend fun requireAuthentication(): String {
        // TODO: clear db
        return authRepo.getAuthToken().takeIf { !it.isNullOrBlank() } ?: throw Throwable("Unauthorized")
    }
}