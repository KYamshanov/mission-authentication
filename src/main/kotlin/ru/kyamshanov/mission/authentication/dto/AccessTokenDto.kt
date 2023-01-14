package ru.kyamshanov.mission.authentication.dto

/**
 * Dto-model Для токена доступности
 * @property accessToken Токен доступности
 * @property data Access данные
 */
data class AccessTokenDto(
    val accessToken: String,
    val data: AccessDataDto
)
