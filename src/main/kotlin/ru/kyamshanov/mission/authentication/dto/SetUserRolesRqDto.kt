package ru.kyamshanov.mission.authentication.dto

import ru.kyamshanov.mission.authentication.models.UserRole

/**
 * Dto-model Тело запроса на изменение ролей пользоваетля
 * @property externalUserId Внешний идентификатор пользователя
 * @property roles Список ролей пользователя
 */
data class SetUserRolesRqDto(
    val externalUserId: String,
    val roles: List<UserRole>
)
