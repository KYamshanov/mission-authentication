package ru.kyamshanov.mission.authentication.dto

/**
 * Dto-model Тело запроса на блокировку сессии
 * @property refreshToken Рефреш токен
 */
data class BlockRqDto(
    val refreshToken: String
)
