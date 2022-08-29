package ru.kyamshanov.mission.authentication.dto


/**
 * Dto-Модель - Тело запроса для регистрации пользователя
 * @property login Имя пользователя
 * @property password Пароль
 * @property info Дополнительная информация о пользователе
 */
data class UserDto(
    val login: String,
    val password: CharSequence,
    val info: Map<String, Any>? = null
)