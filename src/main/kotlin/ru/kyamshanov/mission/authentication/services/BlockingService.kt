package ru.kyamshanov.mission.authentication.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.kyamshanov.mission.authentication.propcessors.JwtProcessor

/**
 * Сервис блокировки
 */
internal interface BlockingService {

    /**
     * Блокировать сессию
     * @param refreshToken Refresh токен для блокировки
     */
    suspend fun blockSession(refreshToken: String)
}

/**
 * Реализация [BlockingService]
 * @property jwtProcessor Обработчик JWT
 */
@Service
internal class BlockingServiceImpl @Autowired constructor(
    private val jwtProcessor: JwtProcessor
) : BlockingService {

    override suspend fun blockSession(refreshToken: String) =
        jwtProcessor.blockRefreshToken(refreshToken)
}