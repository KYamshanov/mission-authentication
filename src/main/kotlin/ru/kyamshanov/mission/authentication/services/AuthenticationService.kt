package ru.kyamshanov.mission.authentication.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.kyamshanov.mission.authentication.errors.*
import ru.kyamshanov.mission.authentication.models.JsonMap
import ru.kyamshanov.mission.authentication.models.JwtPair
import ru.kyamshanov.mission.authentication.models.User
import ru.kyamshanov.mission.authentication.propcessors.JwtProcessor
import ru.kyamshanov.mission.authentication.propcessors.UserProcessor

/**
 * Сервис аутентификации
 */
internal interface AuthenticationService {

    /**
     * Аутентифицировать пользователя
     * @param user Модель пользователя
     * @param userInfo Информация о пользователе
     * @throws UserNotFoundException если пользователь не найден в БД
     * @throws IllegalArgumentException Если [User.id] - null
     */
    suspend fun login(user: User, userInfo: JsonMap): JwtPair

    /**
     * Проверить access токен
     * @param accessToken токен
     * @throws TokenTypeException Если тип токена не ACCESS
     * @throws TokenExpireException Если время действия токена истекло
     */
    fun verifyAccess(accessToken: String)

    /**
     * Проверить и обновить refresh токен
     * @param refreshToken токен
     * @param userInfo Информация о пользователе
     * @throws TokenTypeException Если тип токена не соответствует REFRESH
     * @throws TokenNotFoundException Если сессия не найдена
     * @throws TokenExpireException Если время действия токена истекло
     * @throws TokenStatusException Если статус токена не ACTIVE
     * @throws UserVerifyException Ошибка если [userInfo] не соответствует сохраненной
     *
     * @return Новую пару JWT токенов [JwtPair]
     */
    suspend fun verifyAndUpdateRefreshToken(refreshToken: String, userInfo: JsonMap): JwtPair
}

/**
 * Реализация [AuthenticationService]
 * @property userProcessor Обработчик пользоваетля
 * @property jwtProcessor Обработчик JWT
 */
@Service
internal class AuthenticationServiceImpl @Autowired constructor(
    private val userProcessor: UserProcessor,
    private val jwtProcessor: JwtProcessor
) : AuthenticationService {

    override suspend fun login(user: User, userInfo: JsonMap): JwtPair =
        userProcessor.verify(user).let { jwtProcessor.createSession(it, userInfo) }

    override fun verifyAccess(accessToken: String) {
        jwtProcessor.verifyAccessToken(accessToken)
    }

    override suspend fun verifyAndUpdateRefreshToken(refreshToken: String, userInfo: JsonMap) =
        jwtProcessor.refreshSession(refreshToken, userInfo)
}