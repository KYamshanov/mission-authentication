package ru.kyamshanov.mission.authentication.dto

/**
 * Dto-model Тело запроса на блокировку сессии по рефреш токена
 * @property refreshToken Рефреш токен
 */
data class BlockRefreshRqDto(
    val refreshToken: String
)
