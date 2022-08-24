package ru.kyamshanov.mission.authentication.dto

/**
 * Dto-model Тело запроса на проверку активности токена
 * @property accessToken Токен доступности
 */
data class CheckAccessRqDto(
    val accessToken: String
)
