package ru.kyamshanov.mission.authentication.propcessors

import ru.kyamshanov.mission.authentication.GlobalConstants.ACCESS_TOKEN_TYPE
import ru.kyamshanov.mission.authentication.GlobalConstants.REFRESH_TOKEN_TYPE
import ru.kyamshanov.mission.authentication.components.DecodeJwtTokenUseCase
import ru.kyamshanov.mission.authentication.components.GenerateJwtTokenUseCase
import ru.kyamshanov.mission.authentication.components.GetCurrentInstantUseCase
import ru.kyamshanov.mission.authentication.components.GetCurrentLocalDateTimeUseCase
import ru.kyamshanov.mission.authentication.entities.TokenEntity
import ru.kyamshanov.mission.authentication.entities.TokenStatus
import ru.kyamshanov.mission.authentication.errors.*
import ru.kyamshanov.mission.authentication.models.*
import ru.kyamshanov.mission.authentication.repositories.TokenSafeRepository
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit.SECONDS
import java.util.*

/**
 * Обработчик JWT
 */
internal interface JwtProcessor {

    /**
     * Создать сессию
     * @param user Модель пользователя
     * @return [JwtPair] Пара Jwt токенов access/refresh
     */
    suspend fun createSession(user: User, userInfo: JsonMap): JwtPair

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
     * @return Конвертированный токен из [String] в [JwtTokenModel]
     */
    suspend fun verifyRefreshToken(refreshToken: String): JwtTokenModel

    /**
     * Блокировать рефреш токен
     * @param refreshToken Токен для блокировки
     * @throws TokenTypeException если тип токена не REFRESH
     * @throws TokenNotFoundException Если токен не найден в БД
     */
    suspend fun blockRefreshToken(refreshToken: String)
}

/**
 * Реализация [JwtProcessor]
 * @property refreshTokenTimeLife Время жизки refresh токена в сек.
 * @property accessTokenTimeLife Время жизни access токена в сек.
 * @property tokenSafeRepository Безопасный репозиторий для сохранения/получения токенов
 * @property getCurrentInstantUseCase UseCase для получения текущей даты в формате [Instant]
 * @property getCurrentLocalDateTimeUseCase UseCase для получения текущей даты в формате [LocalDateTime]
 * @property GenerateJwtTokenUseCase UseCase для генерации JWT токена
 * @property userVerifyProcessor Средство для проверки данных пользователя
 */
internal class JwtProcessorImpl(
    private val refreshTokenTimeLife: Long,
    private val accessTokenTimeLife: Long,
    private val tokenSafeRepository: TokenSafeRepository,
    private val getCurrentLocalDateTimeUseCase: GetCurrentLocalDateTimeUseCase,
    private val getCurrentInstantUseCase: GetCurrentInstantUseCase,
    private val generateJwtTokenUseCase: GenerateJwtTokenUseCase,
    private val decodeJwtTokenUseCase: DecodeJwtTokenUseCase,
    private val userVerifyProcessor: UserVerifyProcessor
) : JwtProcessor {
    override suspend fun createSession(user: User, userInfo: JsonMap): JwtPair {
        requireNotNull(user.id)
        if (userVerifyProcessor.checkInfo(userInfo).not()) throw IllegalArgumentException("userInfo is invalid")

        val accessToken = newAccessToken(user.login)
        val refreshTokenEntity = generateRefreshTokenEntity(userId = user.id, userInfo = userInfo)
        val refreshToken = refreshTokenEntity.toRefreshToken(userLogin = user.login)

        tokenSafeRepository.save(refreshTokenEntity)
        return JwtPair(accessToken = accessToken, refreshToken = refreshToken)
    }


    override fun verifyAccessToken(accessToken: String): Boolean {
        val decodedJWT = decodeJwtTokenUseCase.verify(accessToken)
        if (decodedJWT.type != ACCESS_TOKEN_TYPE) throw TokenTypeException("required token type $ACCESS_TOKEN_TYPE but was find ${decodedJWT.type}")
        if (getCurrentInstantUseCase() > decodedJWT.expiresAt) throw TokenExpireException()
        return true
    }

    override suspend fun verifyRefreshToken(refreshToken: String): JwtTokenModel =
        refreshToken.parseRefreshToken().second.toModel()

    override suspend fun blockRefreshToken(refreshToken: String) {
        val decodedJWT = decodeJwtTokenUseCase.decode(refreshToken)
        if (decodedJWT.type != REFRESH_TOKEN_TYPE) throw TokenTypeException("required token type $REFRESH_TOKEN_TYPE but was find ${decodedJWT.type}")
        (tokenSafeRepository.findById(decodedJWT.jwtId) ?: throw TokenNotFoundException())
            .copy(status = TokenStatus.PAUSED)
            .also { tokenSafeRepository.save(it) }
    }

    override suspend fun refreshSession(refreshToken: String, userInfo: JsonMap): JwtPair {
        val (decodedJWT, token) = refreshToken.parseRefreshToken()
        if (!userVerifyProcessor.verify(requireNotNull(token.sessionInfo), userInfo)) throw UserVerifyException()

        val userLogin = decodedJWT.subject.orEmpty()
        val accessToken = newAccessToken(userLogin)
        val refreshedTokenEntity = generateRefreshTokenEntity(token.userId, userInfo)
        val refreshedToken = refreshedTokenEntity.toRefreshToken(userLogin)

        if (token.sessionInfo != userInfo) {
            token.copy(status = TokenStatus.INVALID, updatedAt = getCurrentLocalDateTimeUseCase())
                .also { tokenSafeRepository.saveTokens(it, refreshedTokenEntity) }
        } else {
            refreshedTokenEntity.copy(givenId = token.id, createdAt = token.createdAt)
                .also { tokenSafeRepository.save(it) }
        }
        return JwtPair(accessToken, refreshedToken)
    }

    private fun generateRefreshTokenEntity(userId: String, userInfo: JsonMap): TokenEntity {
        val createdAt = getCurrentLocalDateTimeUseCase()
        val expiresAt = getCurrentLocalDateTimeUseCase().plusSeconds(refreshTokenTimeLife)

        return TokenEntity(
            userId = userId,
            createdAt = createdAt,
            updatedAt = createdAt,
            refreshExpiresAt = expiresAt,
            status = TokenStatus.ACTIVE,
            sessionInfo = userInfo
        )
    }

    private fun newAccessToken(userLogin: String): String = JwtModel(
        jwtId = UUID.randomUUID().toString(),
        subject = userLogin,
        expiresAt = getCurrentInstantUseCase().plus(accessTokenTimeLife, SECONDS),
        type = ACCESS_TOKEN_TYPE
    ).let { generateJwtTokenUseCase(it) }

    private fun TokenEntity.toRefreshToken(userLogin: String) = JwtModel(
        jwtId = id,
        type = REFRESH_TOKEN_TYPE,
        expiresAt = refreshExpiresAt.atZone(ZoneId.systemDefault()).toInstant(),
        subject = userLogin
    ).let { generateJwtTokenUseCase(it) }

    private suspend fun String.parseRefreshToken(): Pair<JwtModel, TokenEntity> {
        val decodedJWT = decodeJwtTokenUseCase.decode(this)
        if (decodedJWT.type != REFRESH_TOKEN_TYPE) throw TokenTypeException("required token type $REFRESH_TOKEN_TYPE but was find ${decodedJWT.type}")
        val token = tokenSafeRepository.findById(decodedJWT.jwtId) ?: throw TokenNotFoundException()
        if (getCurrentLocalDateTimeUseCase() > token.refreshExpiresAt) throw TokenExpireException()
        if (token.status != TokenStatus.ACTIVE) throw TokenStatusException("current token status ${token.status} but needed ACTIVE")
        return decodedJWT to token
    }
}
