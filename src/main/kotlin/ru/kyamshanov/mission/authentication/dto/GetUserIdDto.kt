package ru.kyamshanov.mission.authentication.dto

/**
 * Dto-model Тело запроса на получение идентификатора пользователя
 * @property refreshToken Токен обновления
 */
data class GetUserIdRqDto(
    val refreshToken: String
)

/**
 * Dto-model Результат запроса на получение идентификатора пользователя
 * @property externalId Идентификатор пользователя
 */
data class GetUserIdRsDto(
    val externalId: String
)
