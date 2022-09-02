package ru.kyamshanov.mission.authentication.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import ru.kyamshanov.mission.authentication.GlobalConstants

/**
 * Конфигурация  Redis
 * @property host Хост для доступа к redis
 * @property port Порт для доступа к redis
 */
@Configuration
internal class RedisConfiguration(
    @Value("\${${GlobalConstants.KEY_REDIS_HOST}}")
    private val host: String,
    @Value("\${${GlobalConstants.KEY_REDIS_PORT}}")
    private val port: Int
) {

    @Bean
    fun reactiveRedisConnectionFactory(): ReactiveRedisConnectionFactory =
        RedisStandaloneConfiguration()
            .apply {
                hostName = this@RedisConfiguration.host
                port = this@RedisConfiguration.port
            }
            .let { LettuceConnectionFactory(it) }

    @Bean
    fun <F, S> reactiveRedisTemplate(
        redisConnectionFactory: ReactiveRedisConnectionFactory,
        objectMapper: ObjectMapper
    ): ReactiveRedisTemplate<F, S> =
        RedisSerializationContext.newSerializationContext<F, S>(GenericJackson2JsonRedisSerializer(objectMapper))
            .build()
            .let { ReactiveRedisTemplate(redisConnectionFactory, it) }
}