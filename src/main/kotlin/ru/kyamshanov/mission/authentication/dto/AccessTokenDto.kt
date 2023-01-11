package ru.kyamshanov.mission.authentication.dto

import ru.kyamshanov.mission.authentication.models.UserRole

/**
 * Dto-model Для токена доступности
 * @property accessToken Токен доступности
 * @property roles Роли пользователя
 */
data class AccessTokenDto(
    val accessToken: String,
    val roles: List<UserRole>
)
