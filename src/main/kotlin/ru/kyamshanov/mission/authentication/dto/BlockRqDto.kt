package ru.kyamshanov.mission.authentication.dto

/**
 * Dto-model Тело запроса на блокировку сессии
 * @property token Рефреш токен
 */
data class BlockRqDto(
    val token: String
)
