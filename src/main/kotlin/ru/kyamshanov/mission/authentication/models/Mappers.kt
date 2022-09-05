package ru.kyamshanov.mission.authentication.models

import ru.kyamshanov.mission.authentication.entities.SessionEntity
import ru.kyamshanov.mission.authentication.entities.UserEntity

/**
 * Конвертировать сущность пользователя таблицы в модель
 * [UserEntity] -> [User]
 */
internal fun UserEntity.toModel(): User = User(login, MASKED_PASSWORD, id)
/*
*//**
 * Конвертировать сущность сессионного токена в модель
 * [SessionEntity] -> [SessionModel]
 *//*
internal fun SessionEntity.toModel(): SessionModel = SessionModel(
    userId = userId,
    createdAt = createdAt,
    updatedAt = updatedAt,
    expiresAt = expiresAt,
    refreshId = refreshId,
    info = sessionInfo
)*/

private const val MASKED_PASSWORD = "MaSkEd"