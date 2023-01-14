package ru.kyamshanov.mission.authentication.dto

import ru.kyamshanov.mission.authentication.models.UserRole

/**
 * Dto-model Access данных
 * @property userId Внешний ID пользователя
 * @property roles Роли пользователя
 */
data class AccessDataDto(
    val roles: List<UserRole>,
    val userId: String
)
