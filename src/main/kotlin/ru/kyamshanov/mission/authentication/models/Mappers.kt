package ru.kyamshanov.mission.authentication.models

import ru.kyamshanov.mission.authentication.entities.TokenEntity
import ru.kyamshanov.mission.authentication.entities.UserEntity

/**
 * Конвертировать сущность пользователя таблицы в модель
 * [UserEntity] -> [User]
 */
internal fun UserEntity.toModel(): User = User(login, MASKED_PASSWORD, id)

/**
 * Конвертировать сущность сессионного токена в модель
 * [TokenEntity] -> [JwtTokenModel]
 */
internal fun TokenEntity.toModel(): JwtTokenModel = JwtTokenModel(
    tokenId = id,
    userId = userId,
    createdAt = createdAt,
    updatedAt = updatedAt,
    expiresAt = refreshExpiresAt
)

private const val MASKED_PASSWORD = "MaSkEd"