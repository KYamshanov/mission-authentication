package ru.kyamshanov.mission.authentication.dto

/**
 * Dto-model Тело ответа с токенами
 * @property accessToken Токен доступности
 * @property refreshToken Токен обновления
 */
data class TokensRsDto(
    val accessToken: AccessTokenDto,
    val refreshToken: String
)
