package ru.kyamshanov.mission.authentication.models

import java.time.Instant

/**
 * Модель JWT токена
 * @property id Идентификатор токена
 * @property userId Идентификатор пользователя токена
 * @property createdAt Дата создания токена
 * @property updatedAt Дата обновления токена
 * @property expiresAt Дата прекращения действия токена
 */
internal data class SessionModel(
    val refreshId: String,
    val userId: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    val expiresAt: Instant,
    val info: JsonMap
)
