package ru.kyamshanov.mission.authentication.dto

import ru.kyamshanov.mission.authentication.models.UserRole

/**
 * Dto-model Access данных
 * @property externalId ID пользователя для внешнего пространства
 * @property roles Роли пользователя
 */
data class AccessDataDto(
    val roles: List<UserRole>,
    val externalId: String
)
