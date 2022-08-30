package ru.kyamshanov.mission.authentication.dto

/**
 * Dto-model Тело запроса для внешней аутентификации
 * @property authShareToken Токен
 * @property info Информация о пользователе
 */
data class LoginShareRqDto(
    val authShareToken: String,
    val info: Map<String, Any>
)
