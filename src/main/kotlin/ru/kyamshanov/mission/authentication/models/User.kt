package ru.kyamshanov.mission.authentication.models

/**
 * Модель пользователя
 * @property login Имя пользователя
 * @property password Пароль
 * @property id Идентификатор пользователя
 */
internal data class User(
    val login: String,
    val password: CharSequence,
    val id: String? = null
)