package ru.kyamshanov.mission.authentication.entities

import org.springframework.data.redis.core.RedisHash
import java.io.Serializable
import java.time.Instant

@RedisHash("blocked_access")
internal data class BlockAccessTokenEntity(
    val id: String, val expiresAt: Instant
) : Serializable