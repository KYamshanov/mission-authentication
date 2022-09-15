package ru.kyamshanov.mission.authentication.dto

/**
 * Dto-model Тело запроса на получение токена внешней аутентификации
 * @property accessToken Access токен
 */
data class ShareRqDto(
    val accessToken: String
)

/**
 * Dto-модель Результата создания share_auth токена
 * @property authToken Токен
 */
data class ShareRsDto(
    val authToken: String
)
