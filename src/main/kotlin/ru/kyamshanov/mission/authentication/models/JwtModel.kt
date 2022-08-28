package ru.kyamshanov.mission.authentication.models

import java.time.Instant

/**
 * Модель описания JWT токена
 * @property jwtId Идентификатор токена
 * @property type Тип токена
 * @property expiresAt Дата прекращения действия токена
 * @property subject Субъект токена
 */
internal data class JwtModel(
    val jwtId: String,
    val type: String,
    val expiresAt: Instant? = null,
    val subject: String? = null
)