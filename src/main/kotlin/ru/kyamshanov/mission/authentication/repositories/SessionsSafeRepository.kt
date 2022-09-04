package ru.kyamshanov.mission.authentication.repositories

import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.kyamshanov.mission.authentication.components.GetCurrentInstantUseCase
import ru.kyamshanov.mission.authentication.entities.RedisBlockedSessionEntity
import ru.kyamshanov.mission.authentication.entities.SessionEntity
import ru.kyamshanov.mission.authentication.entities.EntityStatus
import ru.kyamshanov.mission.authentication.entities.SessionTokenEntity
import ru.kyamshanov.mission.authentication.errors.TokenNotFoundException

/**
 * Безопасный репозиторий для хранения сущнстей сессионных токен
 * @property sessionsCrudRepository CRUD репозиторий для хранения сущнстей сессионных токен
 */
@Repository
internal class SessionsSafeRepository @Autowired constructor(
    private val sessionsCrudRepository: SessionsCrudRepository,
    private val sessionTokenCrudRepository: SessionTokenCrudRepository,
    private val getCurrentInstantUseCase: GetCurrentInstantUseCase,
    private val redisBlockedSessionsRepository: RedisBlockedSessionsRepository
) {

    @Transactional
    suspend fun blockingSession(sessionId: String): List<SessionEntity> =
        sessionsCrudRepository.setSessionStatus(sessionId, EntityStatus.BLOCKED, getCurrentInstantUseCase()).toList()
            .also { list ->
                if (list.isEmpty()) throw TokenNotFoundException()
                redisBlockedSessionsRepository.save(RedisBlockedSessionEntity(sessionId, list.maxOf { it.expiresAt }))
            }

    suspend fun pausingSession(sessionId: String): List<SessionEntity> =
        sessionsCrudRepository.setSessionStatus(sessionId, EntityStatus.PAUSED, getCurrentInstantUseCase()).toList()

    @Transactional
    suspend fun saveNewSession(session: SessionEntity, sessionTokenEntity: SessionTokenEntity) {
        sessionsCrudRepository.save(session)
        sessionTokenCrudRepository.save(sessionTokenEntity)
    }

    suspend fun deleteExpiredTokens() = sessionsCrudRepository.deleteExpiredTokens(getCurrentInstantUseCase())

    suspend fun findBySessionId(sessionId: String) = sessionsCrudRepository.findById(sessionId)

    @Transactional
    suspend fun saveSessions(vararg sessions: SessionEntity) {
        sessionsCrudRepository.saveAll(sessions.toList()).last()
    }
}