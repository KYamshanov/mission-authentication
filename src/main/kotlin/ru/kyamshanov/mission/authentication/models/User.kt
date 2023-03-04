package ru.kyamshanov.mission.authentication.models

/**
 * Модель пользователя
 * @property login Имя пользователя
 * @property password Пароль
 * @property credentials Права пользователя
 * @property id Идентификатор пользователя
 */
internal data class User(
    val login: String,
    val password: CharSequence,
    val credentials: Credentials = Credentials.Empty,
    val id: String? = null
)