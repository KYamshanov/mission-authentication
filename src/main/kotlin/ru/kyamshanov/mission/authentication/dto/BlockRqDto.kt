package ru.kyamshanov.mission.authentication.dto

/**
 * Dto-model Тело запроса на блокировку токена
 * @property token Токен
 */
data class BlockRqDto(
    val token: String
)
