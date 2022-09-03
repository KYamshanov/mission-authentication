package ru.kyamshanov.mission.authentication.models

import ru.kyamshanov.mission.authentication.entities.SessionEntity
import ru.kyamshanov.mission.authentication.entities.UserEntity

/**
 * Конвертировать сущность пользователя таблицы в модель
 * [UserEntity] -> [User]
 */
internal fun UserEntity.toModel(): User = User(login, MASKED_PASSWORD, id)

/**
 * Конвертировать сущность сессионного токена в модель
 * [SessionEntity] -> [JwtTokenModel]
 */
internal fun SessionEntity.toModel(): JwtTokenModel = JwtTokenModel(
    tokenId = id,
    userId = userId,
    createdAt = createdAt,
    updatedAt = updatedAt,
    expiresAt = expiresAt
)

private const val MASKED_PASSWORD = "MaSkEd"