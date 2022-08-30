package ru.kyamshanov.mission.authentication.dto

/**
 * Dto-model Тело запроса на проверку активности токена
 * @property accessToken Токен доступности
 * @property checkBlock Тогл проверки блокировки токена
 */
data class CheckAccessRqDto(
    val accessToken: String,
    val checkBlock: Boolean = false
)
