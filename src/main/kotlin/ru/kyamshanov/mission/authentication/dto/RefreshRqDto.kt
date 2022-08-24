package ru.kyamshanov.mission.authentication.dto

/**
 * Dto-model Тело запроса на обновление токенов
 * @property info Информация о пользователе
 * @property refreshToken Токен обновления
 */
data class RefreshRqDto(
    val info: Map<String, Any>,
    val refreshToken: String
)
