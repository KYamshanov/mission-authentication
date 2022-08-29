package ru.kyamshanov.mission.authentication.errors

/**
 * Исключение: Токен не найден в БД
 */
internal class TokenNotFoundException : Exception {

    constructor() : super()
    constructor(message: String?) : super(message)
}