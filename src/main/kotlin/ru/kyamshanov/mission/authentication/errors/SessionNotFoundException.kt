package ru.kyamshanov.mission.authentication.errors

/**
 * Исключение: Сессия не найдена
 */
internal class SessionNotFoundException : Exception {

    constructor() : super()
    constructor(message: String?) : super(message)
}