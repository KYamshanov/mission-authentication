package ru.kyamshanov.mission.authentication.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.kyamshanov.mission.authentication.GlobalConstants
import ru.kyamshanov.mission.authentication.components.DecodeJwtTokenUseCase
import ru.kyamshanov.mission.authentication.errors.SessionBlockedException
import ru.kyamshanov.mission.authentication.models.JwtModel
import ru.kyamshanov.mission.authentication.repositories.RedisBlockedSessionsRepository
import ru.kyamshanov.mission.authentication.repositories.SessionsSafeRepository


internal interface VerifyService {

    suspend fun verifyAccessToken(accessToken: String, checkBlock: Boolean): JwtModel
}

@Service
private class VerifyServiceImpl @Autowired constructor(
    private val sessionsSafeRepository: SessionsSafeRepository,
    private val decodeJwtTokenUseCase: DecodeJwtTokenUseCase,
    private val redisBlockedSessionsRepository: RedisBlockedSessionsRepository
) : VerifyService {

    override suspend fun verifyAccessToken(accessToken: String, checkBlock: Boolean): JwtModel {
        val jwtModel = decodeJwtTokenUseCase.verify(accessToken, GlobalConstants.ACCESS_TOKEN_TYPE)
        if (checkBlock && redisBlockedSessionsRepository.existsBySessionId(jwtModel.jwtId)) throw SessionBlockedException(
            "Session with id ${jwtModel.jwtId} was blocked"
        )
        return jwtModel
    }
}