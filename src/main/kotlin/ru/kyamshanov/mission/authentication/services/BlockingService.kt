package ru.kyamshanov.mission.authentication.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.kyamshanov.mission.authentication.GlobalConstants
import ru.kyamshanov.mission.authentication.components.DecodeJwtTokenUseCase
import ru.kyamshanov.mission.authentication.components.ExpireVerificationValidator
import ru.kyamshanov.mission.authentication.entities.BlockAccessTokenEntity
import ru.kyamshanov.mission.authentication.errors.TokenTypeException
import ru.kyamshanov.mission.authentication.propcessors.JwtProcessor
import ru.kyamshanov.mission.authentication.repositories.BlockedAccessTokenRepository

/**
 * Сервис блокировки
 */
internal interface BlockingService {

    /**
     * Блокировать сессию
     * @param refreshToken Refresh токен для блокировки
     */
    suspend fun blockSession(refreshToken: String)

    suspend fun blockAccess(accessToken: String)

    suspend fun verifyAccess(accessToken: String): Boolean
}

/**
 * Реализация [BlockingService]
 * @property jwtProcessor Обработчик JWT
 */
@Service
internal class BlockingServiceImpl @Autowired constructor(
    private val jwtProcessor: JwtProcessor,
    private val decodeJwtTokenUseCase: DecodeJwtTokenUseCase,
    private val expireVerificationValidator: ExpireVerificationValidator,
    private val blockedAccessTokenRepository: BlockedAccessTokenRepository
) : BlockingService {

    override suspend fun blockSession(refreshToken: String) =
        jwtProcessor.blockRefreshToken(refreshToken)

    override suspend fun blockAccess(accessToken: String) {
        val blockEntity = decodeJwtTokenUseCase.decode(accessToken).let {
            expireVerificationValidator(requireNotNull(it.expiresAt))
            if (it.type != GlobalConstants.ACCESS_TOKEN_TYPE) throw TokenTypeException("required ${GlobalConstants.ACCESS_TOKEN_TYPE} token type but found ${it.type}")
            BlockAccessTokenEntity(it.jwtId, it.expiresAt)
        }
        blockedAccessTokenRepository.save(blockEntity)
    }

    override suspend fun verifyAccess(accessToken: String): Boolean {
        val jwt = decodeJwtTokenUseCase.decode(accessToken).also {
            if (it.type != GlobalConstants.ACCESS_TOKEN_TYPE) throw TokenTypeException("required ${GlobalConstants.ACCESS_TOKEN_TYPE} token type but found ${it.type}")
        }
        return blockedAccessTokenRepository.findById(jwt.jwtId) != null
    }
}