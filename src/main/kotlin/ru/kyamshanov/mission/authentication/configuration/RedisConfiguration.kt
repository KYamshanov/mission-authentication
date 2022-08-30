package ru.kyamshanov.mission.authentication.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate


@Configuration
internal class RedisConfiguration {

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory =
        RedisStandaloneConfiguration()
            .apply {
                hostName = "localhost"
                port = 6379
            }
            .let { LettuceConnectionFactory(it) }

    @Bean
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> =
        RedisTemplate<String, Any>()
            .apply {
                setConnectionFactory(redisConnectionFactory)
            }
}