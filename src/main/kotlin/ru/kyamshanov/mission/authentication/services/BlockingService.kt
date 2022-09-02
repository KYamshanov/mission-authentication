package ru.kyamshanov.mission.authentication.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.kyamshanov.mission.authentication.components.GetCurrentInstantUseCase
import ru.kyamshanov.mission.authentication.configuration.AccessTokenTimeLifeInSec
import ru.kyamshanov.mission.authentication.errors.TokenNotFoundException
import ru.kyamshanov.mission.authentication.propcessors.SessionProcessor
import ru.kyamshanov.mission.authentication.repositories.SessionsSafeRepository

/**
 * Сервис блокировки
 */
internal interface BlockingService {

    /**
     * Блокировать сессию
     * @param sessionId Идентификатор сессии
     */
    suspend fun blockSession(sessionId: String)
}

/**
 * Реализация [BlockingService]
 * @property sessionProcessor Обработчик JWT
 * @property sessionsSafeRepository Безопасный репозиторий для взаимодействия с сессиями
 * @property accessTokenTimeLifeInSec Время жизни access токена в сек.
 * @property getCurrentInstantUseCase UseCase для получения текущй отметки времени
 */
@Service
private class BlockingServiceImpl @Autowired constructor(
    private val sessionProcessor: SessionProcessor,
    private val sessionsSafeRepository: SessionsSafeRepository,
    @AccessTokenTimeLifeInSec
    private val accessTokenTimeLifeInSec: Long,
    private val getCurrentInstantUseCase: GetCurrentInstantUseCase
) : BlockingService {

    override suspend fun blockSession(sessionId: String) {
        runCatching {
            sessionsSafeRepository.blockingSession(
                sessionId,
                getCurrentInstantUseCase().plusSeconds(accessTokenTimeLifeInSec)
            )
        }.onFailure {
            if (it !is TokenNotFoundException) {
                sessionsSafeRepository.pausingSession(sessionId)
                throw it
            }
        }
    }
}