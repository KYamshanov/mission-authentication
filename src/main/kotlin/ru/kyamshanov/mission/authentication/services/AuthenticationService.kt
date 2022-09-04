package ru.kyamshanov.mission.authentication.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.kyamshanov.mission.authentication.GlobalConstants
import ru.kyamshanov.mission.authentication.components.GenerateJwtTokenUseCase
import ru.kyamshanov.mission.authentication.components.GetCurrentInstantUseCase
import ru.kyamshanov.mission.authentication.configuration.AccessTokenTimeLifeInSec
import ru.kyamshanov.mission.authentication.configuration.RefreshTokenTimeLifeInSec
import ru.kyamshanov.mission.authentication.entities.EntityStatus
import ru.kyamshanov.mission.authentication.entities.SessionEntity
import ru.kyamshanov.mission.authentication.entities.SessionTokenEntity
import ru.kyamshanov.mission.authentication.errors.*
import ru.kyamshanov.mission.authentication.models.JsonMap
import ru.kyamshanov.mission.authentication.models.JwtModel
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
     * @param user Модель пользователя
     * @param userInfo Информация о пользователе
     * @throws UserNotFoundException если пользователь не найден в БД
     * @throws IllegalArgumentException Если [User.id] - null
     */
    suspend fun login(user: User, userInfo: JsonMap): JwtPair

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
 * @property sessionProcessor Обработчик JWT
 */
@Service
internal class AuthenticationServiceImpl @Autowired constructor(
    private val userProcessor: UserProcessor,
    private val sessionProcessor: SessionProcessor,
    private val getCurrentInstantUseCase: GetCurrentInstantUseCase,
    private val userVerifyProcessor: UserVerifyProcessor,
    @RefreshTokenTimeLifeInSec
    private val refreshTokenTimeLifeInSec: Long,
    @AccessTokenTimeLifeInSec
    private val accessTokenTimeLifeInSec: Long,
    private val generateJwtTokenUseCase: GenerateJwtTokenUseCase,
    private val safeSessionsSafeRepository: SessionsSafeRepository
) : AuthenticationService {

    override suspend fun login(userSketch: User, userInfo: JsonMap): JwtPair {
        if (userVerifyProcessor.checkInfo(userInfo).not()) throw IllegalArgumentException("userInfo is invalid")
        val user = userProcessor.verify(userSketch)
        val sessionEntity = createSessionEntity(requireNotNull(user.id))
        val sessionTokenEntity = sessionEntity.toTokenEntity(userInfo)
        val accessToken = sessionEntity.toAccessToken(user.login)
        val refreshToken = sessionTokenEntity.toRefreshToken(user.login)

        safeSessionsSafeRepository.saveNewSession(sessionEntity, sessionTokenEntity)
        return JwtPair(accessToken, refreshToken)
    }

    override suspend fun refreshSession(refreshToken: String, userInfo: JsonMap) =
        sessionProcessor.refreshSession(refreshToken, userInfo)

    private fun createSessionEntity(userId: String): SessionEntity {
        val now = getCurrentInstantUseCase()
        return SessionEntity(
            userId = userId,
            createdAt = now,
            updatedAt = now,
            status = EntityStatus.ACTIVE
        )
    }

    private fun SessionEntity.toTokenEntity(userInfo: JsonMap): SessionTokenEntity {
        val now = getCurrentInstantUseCase()
        return SessionTokenEntity(
            sessionId = id,
            createdAt = now,
            expiresAt = now.plusSeconds(refreshTokenTimeLifeInSec),
            userInfo = userInfo
        )
    }

    private fun SessionTokenEntity.toRefreshToken(login: String): String = JwtModel(
        jwtId = id,
        type = GlobalConstants.REFRESH_TOKEN_TYPE,
        expiresAt = expiresAt,
        subject = login
    ).let { generateJwtTokenUseCase.invoke(it) }

    private fun SessionEntity.toAccessToken(login: String): String {
        val now = getCurrentInstantUseCase()
        return JwtModel(
            jwtId = id,
            type = GlobalConstants.ACCESS_TOKEN_TYPE,
            expiresAt = now.plusSeconds(accessTokenTimeLifeInSec),
            subject = login
        ).let { generateJwtTokenUseCase(it) }
    }
}