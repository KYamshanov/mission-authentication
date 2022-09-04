package ru.kyamshanov.mission.authentication.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.kyamshanov.mission.authentication.GlobalConstants
import ru.kyamshanov.mission.authentication.components.DecodeJwtTokenUseCase
import ru.kyamshanov.mission.authentication.components.ExpireVerificationValidator
import ru.kyamshanov.mission.authentication.entities.BlockAccessTokenEntity
import ru.kyamshanov.mission.authentication.entities.TokenStatus
import ru.kyamshanov.mission.authentication.errors.TokenNotFoundException
import ru.kyamshanov.mission.authentication.errors.TokenTypeException
import ru.kyamshanov.mission.authentication.propcessors.SessionProcessor
import ru.kyamshanov.mission.authentication.repositories.RedisBlockedSessionsRepository
import ru.kyamshanov.mission.authentication.repositories.SessionsSafeRepository
import java.time.Instant

/**
 * Сервис блокировки
 */
internal interface BlockingService {

    /**
     * Блокировать сессию
     * @param refreshToken Refresh токен для блокировки
     */
    suspend fun blockSession(refreshToken: String)

    /**
     * Блокировать access токен
     * @param accessToken Токен
     */
    suspend fun blockAccess(accessToken: String)
}

/**
 * Реализация [BlockingService]
 * @property sessionProcessor Обработчик JWT
 */
@Service
internal class BlockingServiceImpl @Autowired constructor(
    private val sessionProcessor: SessionProcessor,
    private val decodeJwtTokenUseCase: DecodeJwtTokenUseCase,
    private val expireVerificationValidator: ExpireVerificationValidator,
    private val redisBlockedSessionsRepository: RedisBlockedSessionsRepository,
    private val sessionsSafeRepository: SessionsSafeRepository
) : BlockingService {

    override suspend fun blockSession(refreshToken: String) {
        val sessionEntity = decodeJwtTokenUseCase.decode(refreshToken).run {
            (sessionsSafeRepository.findByRefreshId(jwtId) ?: throw TokenNotFoundException())
                .copy(status = TokenStatus.PAUSED)
        }
        blockAccessToken(sessionEntity.sessionId, sessionEntity.expiresAt)
        sessionsSafeRepository.save(sessionEntity)
    }

    override suspend fun blockAccess(accessToken: String) {
        val jwtModel = decodeJwtTokenUseCase.decode(accessToken)
        expireVerificationValidator(requireNotNull(jwtModel.expiresAt))
        if (jwtModel.type != GlobalConstants.ACCESS_TOKEN_TYPE) throw TokenTypeException("required ${GlobalConstants.ACCESS_TOKEN_TYPE} token type but found ${jwtModel.type}")
        blockAccessToken(jwtModel.jwtId, jwtModel.expiresAt)
    }

    private suspend fun blockAccessToken(sessionId: String, expiresAt: Instant): Unit = withContext(Dispatchers.IO) {
        val blockEntity = BlockAccessTokenEntity(sessionId, expiresAt)
        redisBlockedSessionsRepository.save(blockEntity)
    }
}