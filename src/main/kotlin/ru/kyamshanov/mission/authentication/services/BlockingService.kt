package ru.kyamshanov.mission.authentication.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.kyamshanov.mission.authentication.errors.TokenTypeException
import ru.kyamshanov.mission.authentication.propcessors.SessionProcessor
import ru.kyamshanov.mission.authentication.repositories.SessionsSafeRepository

/**
 * Сервис блокировки
 */
internal interface BlockingService {

    /**
     * Блокировать сессию
     * @param refreshToken Refresh токен для блокировки
     */
    suspend fun blockSession(sessionId: String)
}

/**
 * Реализация [BlockingService]
 * @property sessionProcessor Обработчик JWT
 */
@Service
internal class BlockingServiceImpl @Autowired constructor(
    private val sessionProcessor: SessionProcessor,
    private val sessionsSafeRepository: SessionsSafeRepository
) : BlockingService {

    override suspend fun blockSession(sessionId: String) {
        runCatching { sessionsSafeRepository.blockingSession(sessionId) }
            .onFailure {
                if (it !is TokenTypeException) {
                    sessionsSafeRepository.pausingSession(sessionId)
                    throw it
                }
            }
    }
}