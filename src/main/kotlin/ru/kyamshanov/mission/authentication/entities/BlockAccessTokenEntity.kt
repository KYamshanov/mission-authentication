package ru.kyamshanov.mission.authentication.entities

import java.io.Serializable
import java.time.Instant

/**
 * Entity блокировки access токена
 * @property sessionId Идентификатор токена
 * @property expiresAt Дата прекращения действия токена
 */
internal data class BlockAccessTokenEntity(
    val sessionId: String, val expiresAt: Instant
) : Serializable