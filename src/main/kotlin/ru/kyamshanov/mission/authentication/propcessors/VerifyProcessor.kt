package ru.kyamshanov.mission.authentication.propcessors

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import ru.kyamshanov.mission.authentication.GlobalConstants
import ru.kyamshanov.mission.authentication.components.DecodeJwtTokenUseCase
import ru.kyamshanov.mission.authentication.components.ExpireVerificationValidator
import ru.kyamshanov.mission.authentication.errors.TokenStatusException
import ru.kyamshanov.mission.authentication.errors.TokenTypeException
import ru.kyamshanov.mission.authentication.models.JwtModel
import ru.kyamshanov.mission.authentication.repositories.RedisBlockedSessionsRepository

/**
 * Сервис блокировки
 */
internal interface VerifyService {


    /**
     * Проверка активности access токена
     * @param accessToken Токен
     * @param checkBlockingSession Тогл проверять ли блокировку сессии
     */
    suspend fun verifyAccess(accessToken: String, checkBlockingSession: Boolean = false): JwtModel
}

/**
 * Реализация [BlockingService]
 * @property sessionProcessor Обработчик JWT
 */
@Component
internal class VerifyServiceImpl @Autowired constructor(
    private val sessionProcessor: SessionProcessor,
    private val redisBlockedSessionsRepository: RedisBlockedSessionsRepository,
    private val decodeJwtTokenUseCase: DecodeJwtTokenUseCase,
    private val expireVerificationValidator: ExpireVerificationValidator
) : VerifyService {


    override suspend fun verifyAccess(accessToken: String, checkBlockingSession: Boolean): JwtModel {
        val jwtModel = verifyJwtToken(accessToken, GlobalConstants.ACCESS_TOKEN_TYPE)
        if (checkBlockingSession && redisBlockedSessionsRepository.existsBySessionId(jwtModel.jwtId))
            throw TokenStatusException("Session with id ${jwtModel.jwtId} was blocked")
        return jwtModel
    }

    private fun verifyJwtToken(token: String, expectedTokenType: String): JwtModel {
        val decodedJWT = decodeJwtTokenUseCase.verify(token)
        if (decodedJWT.type != expectedTokenType) throw TokenTypeException("required token type $expectedTokenType but actual ${decodedJWT.type}")
        expireVerificationValidator(requireNotNull(decodedJWT.expiresAt))
        return decodedJWT
    }
}