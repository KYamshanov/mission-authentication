package ru.kyamshanov.mission.authentication.propcessors

import org.springframework.stereotype.Component
import ru.kyamshanov.mission.authentication.GlobalConstants
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
 * Обработчик сессии
 */
internal interface SessionProcessor {

    /**
     * Создать сессию
     * @param userId Идентификатор пользователя
     * @param userLogin Логин пользователя
     * @param userInfo Информация о пользователе
     *
     * @return [JwtPair] Пара Jwt токенов access/refresh
     */
    suspend fun createSession(userId: String, userLogin: String, userInfo: JsonMap): JwtPair

    /**
     * Обновить сессию
     * @param sessionId Идентификатор сессии
     * @param userLogin Логин пользователя
     * @param currentUserInfo Информация о пользователе
     *
     * @return [JwtPair] Пара Jwt токенов access/refresh
     *
     * @throws SessionNotFoundException Если сессия не найдена
     * @throws StatusException Если у сессии статус не  ACTIVE
     */
    suspend fun refreshSession(refreshId: String, userLogin: String, currentUserInfo: JsonMap): JwtPair
}

/**
 * Реализация [SessionProcessor]
 * @property sessionsSafeRepository Безопасный репозиторий для сохранения/получения токенов
 * @property getCurrentInstantUseCase UseCase для получения текущей даты в формате [Instant]
 * @property expireVerificationValidator Средство проверки истечения срока действия
 * @property generateJwtTokenUseCase UseCase для генерации JWT токена
 * @property userVerifyProcessor Средство для проверки данных пользователя
 * @property refreshTokenTimeLifeInSec Время жизни refresh токена в сек.
 * @property accessTokenTimeLifeInSec Время жизни access токена в сек.
 */
@Component
private class SessionProcessorImpl(
    private val sessionsSafeRepository: SessionsSafeRepository,
    private val getCurrentInstantUseCase: GetCurrentInstantUseCase,
    private val expireVerificationValidator: ExpireVerificationValidator,
    private val generateJwtTokenUseCase: GenerateJwtTokenUseCase,
    private val userVerifyProcessor: UserVerifyProcessor,
    @RefreshTokenTimeLifeInSec
    private val refreshTokenTimeLifeInSec: Long,
    @AccessTokenTimeLifeInSec
    private val accessTokenTimeLifeInSec: Long,
) : SessionProcessor {

    /**
     * @see [SessionProcessor.createSession]
     */
    override suspend fun createSession(userId: String, userLogin: String, userInfo: JsonMap): JwtPair {
        val sessionEntity = createSessionEntity(userId)
        val sessionTokenEntity = sessionEntity.toTokenEntity(userInfo)
        val accessToken = sessionEntity.toAccessToken(userLogin)
        val refreshToken = sessionTokenEntity.toRefreshToken(userLogin)
        sessionsSafeRepository.saveNewSession(sessionEntity, sessionTokenEntity)
        return JwtPair(accessToken, refreshToken)
    }

    /**
     * @see [SessionProcessor.refreshSession]
     */
    override suspend fun refreshSession(
        refreshId: String,
        userLogin: String,
        currentUserInfo: JsonMap
    ): JwtPair {
        val sessionTokenWithSessionEntity =
            (sessionsSafeRepository.findSessionByRefreshId(refreshId) ?: throw SessionNotFoundException())
                .also {
                    if (it.sessionStatus != EntityStatus.ACTIVE) throw StatusException("required status ${EntityStatus.ACTIVE} but found ${it.sessionStatus}")
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
            refreshId = generateRefreshId(),
            status = status
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
