package ru.kyamshanov.mission.authentication.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.kyamshanov.mission.authentication.errors.SessionNotFoundException
import ru.kyamshanov.mission.authentication.repositories.SessionsSafeRepository

/**
 * Сессионный сервис
 */
internal interface SessionService {

    /**
     * Получить все весии пользователя по access токену
     * @param accessToken Access токен
     * @return Список сессий пользователя
     */
    suspend fun getAllSessionsByAccessToken(accessToken: String): List<String>
}

/**
 * Реализация [SessionService]
 * @property verifyService Сервис проверки
 * @property sessionsSafeRepository Безопасный репозиторий для хранения сущнстей сессионных токен
 */
@Service
private class SessionServiceImpl @Autowired constructor(
    private val verifyService: VerifyService,
    private val sessionsSafeRepository: SessionsSafeRepository
) : SessionService {

    /**
     * @see [SessionService.getAllSessionsByAccessToken]
     */
    override suspend fun getAllSessionsByAccessToken(accessToken: String): List<String> {
        val sessionId = verifyService.verifyAccessToken(accessToken, true).jwtId
        val userId = (sessionsSafeRepository.findSessionById(sessionId)
            ?: throw SessionNotFoundException("Session with id $sessionId not found")).userId
        return sessionsSafeRepository.findAllUserSessions(userId).map { it.id }
    }
}