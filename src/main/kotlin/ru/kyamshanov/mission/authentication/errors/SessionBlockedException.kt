package ru.kyamshanov.mission.authentication.errors

/**
 * Исключение связанное с типом токена
 */
internal class SessionBlockedException(message: String?) : Exception(message)