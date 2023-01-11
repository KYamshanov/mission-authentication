package ru.kyamshanov.mission.authentication.models

/**
 * Пара Jwt токенов access/refresh
 * @property accessJwt Токен доступности
 * @property refreshJwt Токен обновления
 */
internal data class JwtPair(
    val accessJwt: JwtModel,
    val refreshJwt: JwtModel
)