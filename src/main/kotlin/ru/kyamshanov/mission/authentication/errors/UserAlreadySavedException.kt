package ru.kyamshanov.mission.authentication.errors

/**
 * Исключение: Пользователь сохранен ранее
 */
internal class UserAlreadySavedException(message: String?) : UserVerifyException(message)