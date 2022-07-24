package ru.kyamshanov.mission.authentication.models

/**
 * Модель пользователя
 * @property login Имя пользователя
 * @property password Пароль
 */
internal data class User(
    val login: String,
    val password: String
)