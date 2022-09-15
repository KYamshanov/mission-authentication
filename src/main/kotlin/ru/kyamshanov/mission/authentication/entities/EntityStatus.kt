package ru.kyamshanov.mission.authentication.entities

/**
 * Перечесление статусов Entity
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