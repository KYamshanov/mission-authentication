package ru.kyamshanov.mission.authentication.errors

/**
 * Исключение связанное с проверкой пользователя
 */
internal open class UserVerifyException : Exception {

    constructor() : super()
    constructor(message: String?) : super(message)
}