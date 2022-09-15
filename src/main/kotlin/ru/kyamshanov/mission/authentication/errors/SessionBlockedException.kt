package ru.kyamshanov.mission.authentication.errors

/**
 * Исключение: Сессия блокированна
 * @property message Описание ошибки
 */
internal class SessionBlockedException(message: String?) : Exception(message)