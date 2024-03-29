package ru.kyamshanov.mission.authentication.dto

/**
 * Dto-model Тело запроса для получения списка сессий
 * @property accessToken Токен доступности
 */
data class GetAllSessionsRqDto(
    val accessToken: String
)

/**
 * Dto-model Тело ответа на запрос получения списка сессий
 * @property sessions Список сессий пользователя
 */
data class GetAllSessionsRsDto(
    val sessions: List<Session>
) {

    data class Session(
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
}
