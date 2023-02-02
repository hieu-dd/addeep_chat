package net.itanchi.addeep.server.service.event

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import net.itanchi.addeep.server.repository.event.*
import net.itanchi.addeep.server.repository.message.MessageModel
import net.itanchi.addeep.server.repository.message.MessageRepo
import net.itanchi.addeep.server.repository.message.MessageType
import net.itanchi.addeep.server.repository.participant.ParticipantModel
import net.itanchi.addeep.server.repository.participant.ParticipantRepo
import net.itanchi.addeep.server.repository.sticker.StickerRepo
import net.itanchi.addeep.server.service.point.PointService
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class EventService(
    private val eventRepo: EventRepo,
    private val eventActionRepo: EventActionRepo,
    private val messageEventRepo: MessageEventRepo,
    private val messageRepo: MessageRepo,
    private val participantRepo: ParticipantRepo,
    private val pointService: PointService,
    private val stickerRepo: StickerRepo
) {
    suspend fun findActiveEvents(): List<Event> = coroutineScope {
        val activeEvents = eventRepo.findAllByStatusOrderByCreatedAtDesc(Status.Active)
        getEventDetails(activeEvents)
    }

    suspend fun getEvents(
        startedFrom: Instant,
        startedTo: Instant,
        status: Status?,
        page: Long,
        pageSize: Long,
    ): List<Event> = coroutineScope {
        val activeEvents = eventRepo.findAllByStartedAtGreaterThanEqualAndAndCreatedAtLessThanEqualAndStatus(
            startedFrom = startedFrom,
            startedTo = startedTo,
            status = status,
            offset = (page - 1) * pageSize,
            limit = pageSize
        )
        getEventDetails(activeEvents)
    }

    private suspend fun getEventDetails(
        events: List<EventModel>
    ): List<Event> = coroutineScope {
        val eventIds = events.map { it.id }
        events.takeIf { it.isNotEmpty() }?.let {
            val eventData = awaitAll(
                async {
                    eventActionRepo.findAllByEventIdIn(eventIds)
                },
                async {
                    messageEventRepo.findAllByEventIdIn(eventIds)
                }
            )
            val actions = (eventData[0] as List<EventActionModel>).groupBy { it.eventId }
            val messageConditions = (eventData[1] as List<MessageEventModel>).groupBy { it.eventId }
            it.map { event ->
                event.toEvent().apply {
                    this@apply.actions = actions[id]?.map { it.toAction() } ?: listOf()
                    this@apply.messageConditions = messageConditions[id]?.map { it.toMessageCondition() } ?: listOf()
                }
            }
        } ?: listOf()
    }

    suspend fun handleMessageEvent(
        userId: Long,
        actionType: ActionType,
        conversationId: Long,
        messageId: Long
    ): List<Event> = coroutineScope {
        messageRepo.findById(messageId)?.takeIf { it.conversationId == conversationId }?.let { message ->
            val validationData = awaitAll(
                async {
                    findActiveEvents()
                },
                async {
                    participantRepo.findAllByConversationId(conversationId)
                }
            )
            val activeEvents = validationData[0] as List<Event>
            val participants = validationData[1] as List<ParticipantModel>
            participants.find { it.userId == userId } ?: return@let null
            activeEvents.mapNotNull { activeEvent ->
                val stickerId = extractStickerId(message)
                val isValidMessage = !(message.messageType == MessageType.Sticker && stickerId == null)
                if (
                    activeEvent.isValidForMessageType(
                        messageType = message.messageType,
                        actionType = actionType,
                        stickerId = stickerId,
                    ) && isValidMessage
                ) {
                    pointService.addPointHistory(
                        userId = userId,
                        point = activeEvent.actions.find { it.type == actionType }?.points ?: 0,
                        actionType = actionType,
                        eventId = activeEvent.id
                    )
                    activeEvent
                } else null
            }
        } ?: listOf()
    }

    private suspend fun extractStickerId(message: MessageModel): Long? {
        return when (message.messageType) {
            MessageType.Sticker -> {
                val stickerInfo =
                    message.message.split("/").takeIf {
                        it.size >= 2 && it[0].toLongOrNull() != null
                    } ?: return null
                return stickerRepo.findByStickerPackAndImageFile(
                    stickerPack = stickerInfo[0].toLong(),
                    imageFile = stickerInfo[1]
                )?.id
            }
            else -> null
        }
    }
}