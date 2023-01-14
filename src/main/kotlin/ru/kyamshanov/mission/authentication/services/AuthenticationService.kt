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
     * @throws StatusException Если статус токена не ACTIVE
     * @throws UserVerifyException Ошибка если [userInfo] не соответствует сохраненной
     *
     * @return Новую пару JWT токенов [JwtPair]
     */
    suspend fun refreshSession(refreshToken: String, userInfo: JsonMap): JwtPair
}

/**
 * Реализация [AuthenticationService]
 * @property userProcessor Обработчик пользоваетля
 * @property getCurrentInstantUseCase UseCase для получения текущй отметки времени
 * @property userVerifyProcessor Средство для проверки данных пользователя
 * @property generateJwtTokenUseCase UseCase для генерации JWT токена
 * @property decodeJwtTokenUseCase UseCase для декодировки jwt токена
 * @property expireVerificationValidator Средство проверки истечения срока действия
 * @property sessionProcessor Обработчик сессии
 * @property roleService Сервис обработки ролей
 */
@Service
private class AuthenticationServiceImpl @Autowired constructor(
    private val userProcessor: UserProcessor,
    private val getCurrentInstantUseCase: GetCurrentInstantUseCase,
    private val userVerifyProcessor: UserVerifyProcessor,
    private val generateJwtTokenUseCase: GenerateJwtTokenUseCase,
    private val decodeJwtTokenUseCase: DecodeJwtTokenUseCase,
    private val expireVerificationValidator: ExpireVerificationValidator,
    private val sessionProcessor: SessionProcessor,
    private val roleService: RoleService
) : AuthenticationService {

    override suspend fun login(userSketch: User, userInfo: JsonMap): JwtPair {
        if (userVerifyProcessor.checkInfo(userInfo).not()) throw IllegalArgumentException("userInfo is invalid")
        val user = userProcessor.verify(userSketch)
        return sessionProcessor.createSession(
            userId = requireNotNull(user.id),
            externalUserId = requireNotNull(user.externalId),
            userInfo = userInfo,
            userRoles = roleService.getUserRoles(user)
        )
    }

    override suspend fun refreshSession(refreshToken: String, userInfo: JsonMap): JwtPair {
        if (!userVerifyProcessor.checkInfo(userInfo)) throw UserVerifyException()
        val jwtModel = decodeJwtTokenUseCase.verify(refreshToken, REFRESH_TOKEN_TYPE)
        return sessionProcessor.refreshSession(
            refreshId = jwtModel.jwtId,
            externalUserId = jwtModel.externalUserId,
            userRoles = jwtModel.roles,
            currentUserInfo = userInfo
        )
    }
}