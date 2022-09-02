package ru.kyamshanov.mission.authentication.dto

/**
 * Dto-model Тело запроса на блокировку сессии
 * @property sessionId Сессия
 */
data class BlockRqDto(
    val sessionId: String
)
