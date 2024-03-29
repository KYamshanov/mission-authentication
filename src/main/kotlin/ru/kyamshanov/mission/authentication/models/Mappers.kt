package ru.kyamshanov.mission.authentication.models

import ru.kyamshanov.mission.authentication.entities.UserEntity

/**
 * Конвертировать сущность пользователя таблицы в модель
 * [UserEntity] -> [User]
 */
internal fun UserEntity.toModel(): User = User(
    login = login,
    password = MASKED_PASSWORD,
    id = id,
)

private const val MASKED_PASSWORD = "MaSkEd"