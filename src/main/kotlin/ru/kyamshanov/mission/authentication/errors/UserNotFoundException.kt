package ru.kyamshanov.mission.authentication.errors

/**
 * Исключение: пользователь не найден (в БД)
 */
internal class UserNotFoundException(message: String?) : UserVerifyException(message)