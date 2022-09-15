package ru.kyamshanov.mission.authentication.repositories

import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.kyamshanov.mission.authentication.components.GetCurrentInstantUseCase
import ru.kyamshanov.mission.authentication.entities.EntityStatus
import ru.kyamshanov.mission.authentication.entities.RedisBlockedSessionEntity
import ru.kyamshanov.mission.authentication.entities.SessionEntity
import ru.kyamshanov.mission.authentication.entities.SessionTokenEntity
import ru.kyamshanov.mission.authentication.errors.TokenNotFoundException
import ru.kyamshanov.mission.authentication.errors.TokenTypeException
import java.time.Instant

/**
 * Безопасный репозиторий для взаимодействия с сессиями
 * @property sessionsCrudRepository CRUD репозиторий для сущнстей сессий
 * @property sessionTokenCrudRepository CRUD репозиторий для сущностей сессионных токенов
 * @property getCurrentInstantUseCase UseCase для получения текущй отметки времени
 * @property redisBlockedSessionsRepository Репозиторий (Redis) блокированных сессий
 * @property nativeQueryRepository Репозиторий используеющий нативные запросы к БД
 */
@Repository
internal class SessionsSafeRepository @Autowired constructor(
    private val sessionsCrudRepository: SessionsCrudRepository,
    private val sessionTokenCrudRepository: SessionTokenCrudRepository,
    private val getCurrentInstantUseCase: GetCurrentInstantUseCase,
    private val redisBlockedSessionsRepository: RedisBlockedSessionsRepository,
    private val nativeQueryRepository: NativeQueryRepository
) {

    /**
     * Блокировка сессии
     * @param sessionId Идентификатор сессии
     * @param lastAccessTokenExpiresAt Время окончания действия последнего access токена
     */
    @Transactional
    suspend fun blockingSession(sessionId: String, lastAccessTokenExpiresAt: Instant): List<SessionEntity> =
        sessionsCrudRepository.setSessionStatus(sessionId, EntityStatus.BLOCKED, getCurrentInstantUseCase()).toList()
            .also { list ->
                if (list.isEmpty()) throw TokenNotFoundException()
                redisBlockedSessionsRepository.save(RedisBlockedSessionEntity(sessionId, lastAccessTokenExpiresAt))
            }

    /**
     * Приостановить действие сессии
     * @param sessionId Идентификатор сессии
     */
    suspend fun pausingSession(sessionId: String): List<SessionEntity> =
        sessionsCrudRepository.setSessionStatus(sessionId, EntityStatus.PAUSED, getCurrentInstantUseCase()).toList()

    /**
     * Сохранить новую сессию
     * @param session Сущность сессии
     * @param sessionTokenEntity Сущность токена сессии
     */
    @Transactional
    suspend fun saveNewSession(session: SessionEntity, sessionTokenEntity: SessionTokenEntity) {
        sessionsCrudRepository.save(session)
        sessionTokenCrudRepository.save(sessionTokenEntity)
    }

    /**
     * Удалить токены с истекшим сроком действия
     */
    suspend fun deleteExpiredTokens() = sessionTokenCrudRepository.deleteOlderTokens(getCurrentInstantUseCase())

    /**
     * Найти сессию по id
     * @param sessionId Идентификатор сесии
     */
    suspend fun findSessionById(sessionId: String) = sessionsCrudRepository.findById(sessionId)

    /**
     * Найти сессию по id рефреш токена
     * @param refreshId Идентификатор refresh токена
     */
    suspend fun findSessionByRefreshId(refreshId: String) = nativeQueryRepository.findSessionByRefreshId(refreshId)

    /**
     * Сохранить сесии
     * @param sessions Сущности сесиий
     */
    @Transactional
    suspend fun saveSessions(vararg sessions: SessionEntity) {
        sessionsCrudRepository.saveAll(sessions.toList()).last()
    }

    /**
     * Сохранить сущность сессионного токена
     * @param tokenEntity Сущность сессионного токена
     */
    suspend fun saveSessionToken(tokenEntity: SessionTokenEntity) {
        sessionTokenCrudRepository.save(tokenEntity)
    }

    /**
     * Проверить активность сессии
     * @param sessionId Идентификатор сессии
     */
    suspend fun verifyValidationSession(sessionId: String) {
        val session = sessionsCrudRepository.findById(sessionId) ?: throw TokenNotFoundException()
        if (session.status != EntityStatus.ACTIVE) throw TokenTypeException("Session stats is ${session.status} but need ACTIVE")
    }

    /**
     * Найти сессии пользователя
     * @param userId Идентификатор пользователя
     */
    suspend fun findAllUserSessions(userId: String): List<SessionEntity> =
        sessionsCrudRepository.findAllByUserId(userId).toList()
}