package ru.kyamshanov.mission.authentication.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import ru.kyamshanov.mission.authentication.components.GetCurrentInstantUseCase
import ru.kyamshanov.mission.authentication.entities.RedisBlockedSessionEntity
import java.time.Duration

/**
 * Репозиторий (Redis) блокированных сессий
 * @property reactiveRedisTemplate Система для реактивного доступа к данным БД Redis
 * @property getCurrentInstantUseCase UseCase для получения текущй отметки времени
 */
@Repository
internal class RedisBlockedSessionsRepository @Autowired constructor(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, RedisBlockedSessionEntity>,
    private val getCurrentInstantUseCase: GetCurrentInstantUseCase
) {

    /**
     * Сохранить блокировку сессии
     * @param entity Сущность блокировки
     */
    suspend fun save(entity: RedisBlockedSessionEntity) = withContext(Dispatchers.IO) {
        reactiveRedisTemplate.opsForValue().setIfAbsent(
            entity.sessionId,
            entity,
            Duration.ofSeconds(entity.expiresAt.epochSecond - getCurrentInstantUseCase.invoke().epochSecond)
        ).awaitSingleOrNull()
    }

    /**
     * Проверить существует ли сущность с [sessionId] в БД
     * @param sessionId Идентификатор сессии
     */
    suspend fun existsBySessionId(sessionId: String): Boolean = withContext(Dispatchers.IO) {
        reactiveRedisTemplate.opsForValue().get(sessionId).awaitSingleOrNull() != null
    }
}