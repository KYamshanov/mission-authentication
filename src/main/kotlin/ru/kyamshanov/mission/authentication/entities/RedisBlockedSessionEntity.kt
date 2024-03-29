package ru.kyamshanov.mission.authentication.entities

import org.springframework.data.redis.core.RedisHash
import java.io.Serializable
import java.time.Instant

/**
 * Entity Redis-блокировки сессии
 * @property sessionId Идентификатор сессии
 * @property expiresAt Дата удаления записи из Redis (дата действия последнего access токена или время жизни access токена)
 */
@RedisHash
internal data class RedisBlockedSessionEntity(
    val sessionId: String, val expiresAt: Instant
) : Serializable