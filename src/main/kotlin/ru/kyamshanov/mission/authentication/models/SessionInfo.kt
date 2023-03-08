package ru.kyamshanov.mission.authentication.models

internal data class SessionInfo(
    val id: String,
    val status: Status
) {

    enum class Status {

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
}