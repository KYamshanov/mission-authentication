package ru.kyamshanov.mission.authentication.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext


@Configuration
internal class RedisConfiguration {

    @Bean
    fun reactiveRedisConnectionFactory(): ReactiveRedisConnectionFactory =
        RedisStandaloneConfiguration()
            .apply {
                hostName = "localhost"
                port = 6379
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