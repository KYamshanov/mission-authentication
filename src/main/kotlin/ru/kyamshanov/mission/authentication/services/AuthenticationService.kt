package ru.kyamshanov.mission.authentication.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.kyamshanov.mission.authentication.GlobalConstants.REFRESH_TOKEN_TYPE
import ru.kyamshanov.mission.authentication.components.DecodeJwtTokenUseCase
import ru.kyamshanov.mission.authentication.components.ExpireVerificationValidator
import ru.kyamshanov.mission.authentication.components.GenerateJwtTokenUseCase
import ru.kyamshanov.mission.authentication.components.GetCurrentInstantUseCase
import ru.kyamshanov.mission.authentication.errors.*
import ru.kyamshanov.mission.authentication.models.JsonMap
import ru.kyamshanov.mission.authentication.models.JwtPair
import ru.kyamshanov.mission.authentication.models.User
import ru.kyamshanov.mission.authentication.propcessors.SessionProcessor
import ru.kyamshanov.mission.authentication.propcessors.UserProcessor
import ru.kyamshanov.mission.authentication.propcessors.UserVerifyProcessor
import ru.kyamshanov.mission.authentication.repositories.SessionsSafeRepository

/**
 * Сервис аутентификации
 */
internal interface AuthenticationService {

    /**
     * Аутентифицировать пользователя
     * @param userSketch Модель пользователя
     * @param userInfo Информация о пользователе
     * @throws UserNotFoundException если пользователь не найден в БД
     * @throws IllegalArgumentException Если [User.id] - null
     */
    suspend fun login(userSketch: User, userInfo: JsonMap): JwtPair

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
    suspend fun refreshSession(refreshToken: String, userInfo: JsonMap): JwtPair
}

/**
 * Реализация [AuthenticationService]
 * @property userProcessor Обработчик пользоваетля
 */
@Service
private class AuthenticationServiceImpl @Autowired constructor(
    private val userProcessor: UserProcessor,
    private val getCurrentInstantUseCase: GetCurrentInstantUseCase,
    private val userVerifyProcessor: UserVerifyProcessor,
    private val generateJwtTokenUseCase: GenerateJwtTokenUseCase,
    private val safeSessionsSafeRepository: SessionsSafeRepository,
    private val decodeJwtTokenUseCase: DecodeJwtTokenUseCase,
    private val expireVerificationValidator: ExpireVerificationValidator,
    private val sessionProcessor: SessionProcessor
) : AuthenticationService {

    override suspend fun login(userSketch: User, userInfo: JsonMap): JwtPair {
        if (userVerifyProcessor.checkInfo(userInfo).not()) throw IllegalArgumentException("userInfo is invalid")
        val user = userProcessor.verify(userSketch)
        return sessionProcessor.createSession(
            userId = requireNotNull(user.id),
            userLogin = user.login,
            userInfo = userInfo
        )
    }

    override suspend fun refreshSession(refreshToken: String, userInfo: JsonMap): JwtPair {
        if (!userVerifyProcessor.checkInfo(userInfo)) throw UserVerifyException()
        val jwtModel = decodeJwtTokenUseCase.verify(refreshToken, REFRESH_TOKEN_TYPE)
        return sessionProcessor.refreshSession(jwtModel.jwtId, jwtModel.subject.orEmpty(), userInfo)
    }
}