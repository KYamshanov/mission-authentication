package ru.kyamshanov.mission.authentication.propcessors

import org.springframework.stereotype.Component
import ru.kyamshanov.mission.authentication.GlobalConstants
import ru.kyamshanov.mission.authentication.components.DecodeJwtTokenUseCase
import ru.kyamshanov.mission.authentication.components.ExpireVerificationValidator
import ru.kyamshanov.mission.authentication.components.GenerateJwtTokenUseCase
import ru.kyamshanov.mission.authentication.components.GetCurrentInstantUseCase
import ru.kyamshanov.mission.authentication.configuration.AccessTokenTimeLifeInSec
import ru.kyamshanov.mission.authentication.configuration.RefreshTokenTimeLifeInSec
import ru.kyamshanov.mission.authentication.entities.*
import ru.kyamshanov.mission.authentication.errors.*
import ru.kyamshanov.mission.authentication.models.*
import ru.kyamshanov.mission.authentication.repositories.SessionsSafeRepository
import java.time.Instant
import java.util.*

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
    suspend fun refreshSession(sessionId: String, userLogin: String, currentUserInfo: JsonMap): JwtPair
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
@Component
internal class SessionProcessorImpl(
    private val sessionsSafeRepository: SessionsSafeRepository,
    private val getCurrentInstantUseCase: GetCurrentInstantUseCase,
    private val expireVerificationValidator: ExpireVerificationValidator,
    private val generateJwtTokenUseCase: GenerateJwtTokenUseCase,
    private val decodeJwtTokenUseCase: DecodeJwtTokenUseCase,
    private val userVerifyProcessor: UserVerifyProcessor,
    @RefreshTokenTimeLifeInSec
    private val refreshTokenTimeLifeInSec: Long,
    @AccessTokenTimeLifeInSec
    private val accessTokenTimeLifeInSec: Long,
) : SessionProcessor {
    override suspend fun createSession(userId: String, userLogin: String, userInfo: JsonMap): JwtPair {
        val sessionEntity = createSessionEntity(userId)
        val sessionTokenEntity = sessionEntity.toTokenEntity(userInfo)
        val accessToken = sessionEntity.toAccessToken(userLogin)
        val refreshToken = sessionTokenEntity.toRefreshToken(userLogin)
        sessionsSafeRepository.saveNewSession(sessionEntity, sessionTokenEntity)
        return JwtPair(accessToken, refreshToken)
    }

    override suspend fun refreshSession(
        sessionId: String,
        userLogin: String,
        currentUserInfo: JsonMap
    ): JwtPair {
        val sessionTokenWithSessionEntity =
            (sessionsSafeRepository.findSessionByRefreshId(sessionId) ?: throw TokenNotFoundException())
                .also {
                    if (it.status != EntityStatus.ACTIVE) throw TokenStatusException("required status ${EntityStatus.ACTIVE} but found ${it.status}")
                    expireVerificationValidator(it.expiresAt)
                }

        val newAccessToken = sessionTokenWithSessionEntity.toSessionEntity().toAccessToken(userLogin)
        val newRefreshToken = sessionTokenWithSessionEntity.toSessionTokenEntity()
            .run {
                val now = getCurrentInstantUseCase()
                copy(
                    givenId = if (this.userInfo != currentUserInfo) null else id,
                    updatedAt = now,
                    refreshId = generateRefreshId(),
                    expiresAt = now.plusSeconds(refreshTokenTimeLifeInSec),
                    userInfo = currentUserInfo
                )
            }.also { sessionsSafeRepository.saveSessionToken(it) }.toRefreshToken(userLogin)
        return JwtPair(newAccessToken, newRefreshToken)
    }

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
            updatedAt = now,
            expiresAt = now.plusSeconds(refreshTokenTimeLifeInSec),
            userInfo = userInfo,
            refreshId = generateRefreshId()
        )
    }

    private fun SessionTokenEntity.toRefreshToken(login: String): String = JwtModel(
        jwtId = refreshId,
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

    private fun generateRefreshId(): String = UUID.randomUUID().toString()

}
