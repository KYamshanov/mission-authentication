package ru.kyamshanov.mission.authentication.models

import java.time.Instant

/**
 * Модель описания JWT токена
 * @property jwtId Идентификатор токена
 * @property type Тип токена
 * @property expiresAt Дата прекращения действия токена
 * @property externalUserId Идентификатор пользвателя за пределами МС (login/USER_ID/..)
 * @property roles Роли пользователя
 */
internal data class JwtModel(
    val jwtId: String,
    val type: String,
    val expiresAt: Instant,
    val externalUserId: String,
    val roles: List<UserRole>,
)