package ru.kyamshanov.mission.authentication.dto

/**
 * Dto-модель Результата создания share_auth токена
 * @property authToken Токен
 */
data class ShareRsDto(
    val authToken: String
)