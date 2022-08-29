package ru.kyamshanov.mission.authentication.entities

/**
 * Перечесление статусов токена
 */
internal enum class TokenStatus {

    /**
     * Активный
     */
    ACTIVE,

    /**
     * Остановленный
     */
    PAUSED,

    /**
     * Недействительный
     */
    INVALID;
}