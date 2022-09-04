package ru.kyamshanov.mission.authentication.entities

/**
 * Перечесление статусов токена
 */
internal enum class EntityStatus {

    /**
     * Активный
     */
    ACTIVE,

    /**
     * Остановленный
     */
    PAUSED,

    /**
     *
     */
    BLOCKED,

    /**
     * Недействительный
     */
    INVALID;
}