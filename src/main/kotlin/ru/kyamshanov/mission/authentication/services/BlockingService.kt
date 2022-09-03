package ru.kyamshanov.mission.authentication.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.kyamshanov.mission.authentication.GlobalConstants
import ru.kyamshanov.mission.authentication.components.DecodeJwtTokenUseCase
import ru.kyamshanov.mission.authentication.components.ExpireVerificationValidator
import ru.kyamshanov.mission.authentication.entities.BlockAccessTokenEntity
import ru.kyamshanov.mission.authentication.errors.TokenStatusException
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

    /**
     * Блокировать access токен
     * @param accessToken Токен
     */
    suspend fun blockAccess(accessToken: String)

    /**
     * Проверка блокировки access токена
     * @param accessToken Токен
     */
    suspend fun verifyAccess(accessToken: String)
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
        withContext(Dispatchers.IO) {
            blockedAccessTokenRepository.save(blockEntity)
        }
    }

    override suspend fun verifyAccess(accessToken: String) {
        decodeJwtTokenUseCase.decode(accessToken).run {
            if (type != GlobalConstants.ACCESS_TOKEN_TYPE) throw TokenTypeException("required ${GlobalConstants.ACCESS_TOKEN_TYPE} token type but found $type")
            if (blockedAccessTokenRepository.existsById(jwtId)) throw TokenStatusException("Token with id $jwtId was blocked")
        }
    }

    private fun
}