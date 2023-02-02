package net.itanchi.addeep.server.config.redis

import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import net.itanchi.addeep.server.rest.convesation.dto.ConversationDTO
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer


@Configuration
class RedisPubSubConfig {
    @Bean
    fun reactiveRedisTemplate(factory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, ConversationDTO> {
        return buildRedisTemplate(factory)
    }

    @Bean
    fun container(factory: ReactiveRedisConnectionFactory): ReactiveRedisMessageListenerContainer {
        return ReactiveRedisMessageListenerContainer(factory)
    }

    private inline fun <reified V : Any> buildRedisTemplate(
        connectionFactory: ReactiveRedisConnectionFactory,
    ): ReactiveRedisTemplate<String, V> {
        val serializationContext = RedisSerializationContext.newSerializationContext<String, V>(StringRedisSerializer())
            .key(StringRedisSerializer())
            .value(
                Kotlin2JsonRedisSerializer(
                    Json {
                        ignoreUnknownKeys = true
                        encodeDefaults = true
                        coerceInputValues = true
                    },
                    Json.serializersModule.serializer<V>(),
                )
            )
            .build()

        return ReactiveRedisTemplate(connectionFactory, serializationContext)
    }
}