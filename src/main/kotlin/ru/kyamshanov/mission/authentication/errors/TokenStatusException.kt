package ru.kyamshanov.mission.authentication.errors

/**
 * Исключение связанное со статусом токена
 */
internal class TokenStatusException(message: String?) : Exception(message)