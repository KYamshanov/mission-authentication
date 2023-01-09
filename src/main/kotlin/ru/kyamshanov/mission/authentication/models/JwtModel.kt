package ru.kyamshanov.mission.authentication.models

import java.time.Instant

/**
 * Модель описания JWT токена
 * @property jwtId Идентификатор токена
 * @property type Тип токена
 * @property expiresAt Дата прекращения действия токена
 * @property subject Субъект токена
 * @property roles Роли пользователя
 */
internal data class JwtModel(
    val jwtId: String,
    val type: String,
    val expiresAt: Instant?,
    val subject: String?,
    val roles: List<UserRole>
)