package ru.kyamshanov.mission.authentication.models

import java.time.LocalDateTime

/**
 * Модель JWT токена
 * @property tokenId Идентификатор токена
 * @property userId Идентификатор пользователя токена
 * @property createdAt Дата создания токена
 * @property updatedAt Дата обновления токена
 * @property expiresAt Дата прекращения действия токена
 */
internal data class JwtTokenModel(
    val tokenId: String,
    val userId: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val expiresAt: LocalDateTime,
)
