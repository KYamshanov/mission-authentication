package ru.kyamshanov.mission.authentication.propcessors

import ru.kyamshanov.mission.authentication.GlobalConstants.ACCESS_TOKEN_TYPE
import ru.kyamshanov.mission.authentication.GlobalConstants.REFRESH_TOKEN_TYPE
import ru.kyamshanov.mission.authentication.components.DecodeJwtTokenUseCase
import ru.kyamshanov.mission.authentication.components.ExpireVerificationValidator
import ru.kyamshanov.mission.authentication.components.GenerateJwtTokenUseCase
import ru.kyamshanov.mission.authentication.components.GetCurrentInstantUseCase
import ru.kyamshanov.mission.authentication.entities.SessionEntity
import ru.kyamshanov.mission.authentication.entities.EntityStatus
import ru.kyamshanov.mission.authentication.errors.*
import ru.kyamshanov.mission.authentication.models.*
import ru.kyamshanov.mission.authentication.repositories.SessionsSafeRepository
import java.time.Instant
import java.time.temporal.ChronoUnit.SECONDS
import java.util.UUID

/**
 * Обработчик JWT
 */
internal interface SessionProcessor {

    /**
     * Создать сессию
     * @param userId Идентификатор пользователя
     * @param userLogin Логин пользователя
     * @return [JwtPair] Пара Jwt токенов access/refresh
     */
    suspend fun createSession(userId: String, userLogin: String, userInfo: JsonMap): JwtPair

    /**
     * Обновить сессию
     * @param refreshToken Рефреш токен
     * @param userInfo Информация о пользователе
     * @return [JwtPair] Новую пару Jwt токенов access/refresh
     * @throws TokenTypeException Если тип токена не соответствует REFRESH
     * @throws TokenNotFoundException Если сессия не найдена
     * @throws TokenExpireException Если время действия токена истекло
     * @throws TokenStatusException Если статус токена не ACTIVE
     * @throws UserVerifyException Ошибка если [userInfo] не соответствует сохраненной
     */
    suspend fun refreshSession(refreshToken: String, userInfo: JsonMap): JwtPair

    /**
     * Проверить активность access токена
     * @param accessToken Токен для проверки
     * @return true если токен активный
     * @throws TokenTypeException Если тип токена не ACCESS
     * @throws TokenExpireException Если время действия токена истекло
     */
    fun verifyAccessToken(accessToken: String): Boolean

    /**
     * Декодировать и проверить refreshToken
     * @param refreshToken Рефреш токен
     * @throws TokenTypeException Если тип токена не REFRESH
     * @throws TokenExpireException Если время действия токена истекло
     * @return Конвертированный токен из [String] в [JwtModel]
     */
    suspend fun verifyRefreshToken(refreshToken: String): SessionModel

    /**
     * Проверить активность refreshToken по id
     * @param sessionId Идентификатор refreshToken
     * @throws TokenTypeException Если тип токена не REFRESH
     * @throws TokenExpireException Если время действия токена истекло
     * @return Информацию о refreshToken если он активный
     */
    suspend fun verifySessionById(sessionId: String): SessionModel

    /**
     * Блокировать рефреш токен
     * @param refreshToken Токен для блокировки
     * @throws TokenTypeException если тип токена не REFRESH
     * @throws TokenNotFoundException Если токен не найден в БД
     */
    suspend fun blockRefreshToken(refreshToken: String)
}

/**
 * Реализация [SessionProcessor]
 * @property refreshTokenTimeLife Время жизки refresh токена в сек.
 * @property accessTokenTimeLife Время жизни access токена в сек.
 * @property sessionsSafeRepository Безопасный репозиторий для сохранения/получения токенов
 * @property getCurrentInstantUseCase UseCase для получения текущей даты в формате [Instant]
 * @property expireVerificationValidator Средство проверки истечения срока действия
 * @property GenerateJwtTokenUseCase UseCase для генерации JWT токена
 * @property userVerifyProcessor Средство для проверки данных пользователя
 */
internal class SessionProcessorImpl(
    private val refreshTokenTimeLife: Long,
    private val accessTokenTimeLife: Long,
    private val sessionsSafeRepository: SessionsSafeRepository,
    private val getCurrentInstantUseCase: GetCurrentInstantUseCase,
    private val expireVerificationValidator: ExpireVerificationValidator,
    private val generateJwtTokenUseCase: GenerateJwtTokenUseCase,
    private val decodeJwtTokenUseCase: DecodeJwtTokenUseCase,
    private val userVerifyProcessor: UserVerifyProcessor
) : SessionProcessor {
    override suspend fun createSession(userId: String, userLogin: String, userInfo: JsonMap): JwtPair {
        if (userVerifyProcessor.checkInfo(userInfo).not()) throw IllegalArgumentException("userInfo is invalid")

        val sessionEntity = generateSessionEntity(userId = userId, userInfo = userInfo)
        val accessToken = sessionEntity.toAccessToken(userId)
        val refreshToken = sessionEntity.toRefreshToken(userLogin)

        sessionsSafeRepository.save(sessionEntity)
        return JwtPair(accessToken = accessToken, refreshToken = refreshToken)
    }


    override fun verifyAccessToken(accessToken: String): Boolean {
        val decodedJWT = decodeJwtTokenUseCase.verify(accessToken)
        if (decodedJWT.type != ACCESS_TOKEN_TYPE) throw TokenTypeException("required token type $ACCESS_TOKEN_TYPE but was find ${decodedJWT.type}")
        expireVerificationValidator(requireNotNull(decodedJWT.expiresAt))
        return true
    }

    override suspend fun verifyRefreshToken(refreshToken: String): SessionModel =
        refreshToken.parseRefreshToken().second.toModel()

    override suspend fun verifySessionById(sessionId: String): SessionModel =
        (sessionsSafeRepository.findBySessionId(sessionId) ?: throw TokenNotFoundException()).apply { validate() }
            .toModel()

    override suspend fun blockRefreshToken(refreshToken: String) {
        val decodedJWT = decodeJwtTokenUseCase.decode(refreshToken)
        if (decodedJWT.type != REFRESH_TOKEN_TYPE) throw TokenTypeException("required token type $REFRESH_TOKEN_TYPE but found ${decodedJWT.type}")
        (sessionsSafeRepository.findByRefreshId(decodedJWT.jwtId) ?: throw TokenNotFoundException())
            .copy(status = EntityStatus.PAUSED)
            .also { sessionsSafeRepository.save(it) }
    }

    override suspend fun refreshSession(refreshToken: String, userInfo: JsonMap): JwtPair {
        val (decodedJWT, token) = refreshToken.parseRefreshToken()
        if (!userVerifyProcessor.verify(requireNotNull(token.sessionInfo), userInfo)) throw UserVerifyException()

        val userLogin = decodedJWT.subject.orEmpty()
        val sessionEntity = generateSessionEntity(sessionId = token.sessionId, token.userId, userInfo)
        val accessToken = sessionEntity.toAccessToken(userLogin)
        val refreshedToken = sessionEntity.toRefreshToken(userLogin)

        if (token.sessionInfo != userInfo) {
            token.copy(status = EntityStatus.INVALID, updatedAt = getCurrentInstantUseCase())
                .also { sessionsSafeRepository.saveSessions(it, sessionEntity) }
        } else {
            sessionEntity.copy(givenId = token.id, createdAt = token.createdAt)
                .also { sessionsSafeRepository.save(it) }
        }
        return JwtPair(accessToken, refreshedToken)
    }

    private fun generateSessionEntity(userId: String): SessionModel {
        val createdAt = getCurrentInstantUseCase()

        return SessionModel()
    }

    private fun SessionEntity.toAccessToken(userLogin: String): String = JwtModel(
        jwtId = id,
        subject = userLogin,
        expiresAt = getCurrentInstantUseCase().plus(accessTokenTimeLife, SECONDS),
        type = ACCESS_TOKEN_TYPE
    ).let { generateJwtTokenUseCase(it) }

    private fun SessionEntity.toRefreshToken(userLogin: String): String = JwtModel(
        jwtId = refreshId,
        type = REFRESH_TOKEN_TYPE,
        expiresAt = expiresAt,
        subject = userLogin
    ).let { generateJwtTokenUseCase(it) }

    private suspend fun String.parseRefreshToken(): Pair<JwtModel, SessionEntity> {
        val decodedJWT = decodeJwtTokenUseCase.decode(this)
        if (decodedJWT.type != REFRESH_TOKEN_TYPE) throw TokenTypeException("required token type $REFRESH_TOKEN_TYPE but was find ${decodedJWT.type}")
        val token =
            (sessionsSafeRepository.findByRefreshId(decodedJWT.jwtId)
                ?: throw TokenNotFoundException()).apply { validate() }
        return decodedJWT to token
    }

    private fun SessionEntity.validate() {
        expireVerificationValidator(expiresAt)
        if (status != EntityStatus.ACTIVE) throw TokenStatusException("current token status $status but needed ACTIVE")
    }
}
