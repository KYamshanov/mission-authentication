package ru.kyamshanov.mission.authentication.dto

/**
 * Dto-model Тело запроса на блокировку токена
 * @property sessionId Сессия
 */
data class BlockRqDto(
    val sessionId: String
)
