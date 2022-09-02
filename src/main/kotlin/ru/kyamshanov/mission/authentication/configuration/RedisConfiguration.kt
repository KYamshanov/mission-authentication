package ru.kyamshanov.mission.authentication.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer


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
    fun reactiveRedisTemplate(redisConnectionFactory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, String> =
        RedisSerializationContext.newSerializationContext<String, String>(StringRedisSerializer()).build().let {
            ReactiveRedisTemplate(redisConnectionFactory, it)
        }
}