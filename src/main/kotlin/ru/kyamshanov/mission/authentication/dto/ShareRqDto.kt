package ru.kyamshanov.mission.authentication.dto

/**
 * Dto-model Тело запроса на токена внешней аутентификации
 * @property refreshToken Рефреш токен
 */
data class ShareRqDto(
    val refreshToken: String
)
