package ru.kyamshanov.mission.authentication.models

/**
 * Пара Jwt токенов access/refresh
 * @property accessToken Токен доступности
 * @property refreshToken Токен обновления
 */
internal data class JwtPair(
    val accessToken: String,
    val refreshToken: String
)