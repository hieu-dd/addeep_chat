package net.itanchi.addeep.server.service.conversation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import net.itanchi.addeep.server.rest.convesation.dto.ConversationDTO
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer
import org.springframework.stereotype.Service


@Service
class RedisPubSubService(
    private val reactiveTemplate: ReactiveRedisTemplate<String, ConversationDTO>,
    private val reactiveMsgListenerContainer: ReactiveRedisMessageListenerContainer
) {
    fun publish(
        message: ConversationDTO,
        topicName: String,
    ) {
        reactiveTemplate
            .convertAndSend(topicName, message)
            .subscribe()
    }

    fun subscribe(
        topicName: String,
    ): Flow<ConversationDTO> {
        return reactiveMsgListenerContainer
            .receive(
                listOf(ChannelTopic(topicName)),
                reactiveTemplate.serializationContext.keySerializationPair,
                reactiveTemplate.serializationContext.valueSerializationPair
            )
            .map { it.message }
            .asFlow()
    }
}
