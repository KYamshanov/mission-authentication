package ru.kyamshanov.mission.authentication.entities

import java.io.Serializable
import java.time.Instant

/**
 * Entity блокировки access токена
 * @property id Идентификатор токена
 * @property expiresAt Дата прекращения действия токена
 */
internal data class BlockAccessTokenEntity(
    val id: String, val expiresAt: Instant
) : Serializable