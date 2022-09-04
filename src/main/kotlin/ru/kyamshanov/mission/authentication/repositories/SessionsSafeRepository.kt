package ru.kyamshanov.mission.authentication.repositories

import kotlinx.coroutines.flow.last
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.kyamshanov.mission.authentication.components.GetCurrentInstantUseCase
import ru.kyamshanov.mission.authentication.entities.SessionEntity

/**
 * Безопасный репозиторий для хранения сущнстей сессионных токен
 * @property sessionsCrudRepository CRUD репозиторий для хранения сущнстей сессионных токен
 */
@Repository
internal class SessionsSafeRepository @Autowired constructor(
    private val sessionsCrudRepository: SessionsCrudRepository,
    private val getCurrentInstantUseCase: GetCurrentInstantUseCase
) {
    suspend fun findByRefreshId(refreshId: String): SessionEntity? =
        sessionsCrudRepository.findByRefreshId(refreshId)

    suspend fun save(session: SessionEntity): SessionEntity =
        sessionsCrudRepository.save(session)

    suspend fun deleteExpiredTokens() = sessionsCrudRepository.deleteExpiredTokens(getCurrentInstantUseCase())

    @Transactional
    suspend fun saveSessions(vararg sessions: SessionEntity) {
        sessionsCrudRepository.saveAll(sessions.toList()).last()
    }
}