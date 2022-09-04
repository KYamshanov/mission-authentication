package ru.kyamshanov.mission.authentication.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import ru.kyamshanov.mission.authentication.components.GetCurrentInstantUseCase
import ru.kyamshanov.mission.authentication.entities.BlockAccessTokenEntity
import java.time.Duration

/**
 * Репозиторий блокировки access токена
 * @property reactiveRedisTemplate Система для реактивного доступа к данным БД Redis
 */
@Repository
internal class RedisBlockedSessionsRepository @Autowired constructor(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, BlockAccessTokenEntity>,
    private val getCurrentInstantUseCase: GetCurrentInstantUseCase
) {

    /**
     * Сохранить блокировку access токена
     * @param entity Сущность блокировки
     */
    suspend fun save(entity: BlockAccessTokenEntity) = withContext(Dispatchers.IO) {
        reactiveRedisTemplate.opsForValue().setIfAbsent(
            entity.sessionId,
            entity,
            Duration.ofSeconds(entity.expiresAt.epochSecond - getCurrentInstantUseCase.invoke().epochSecond)
        ).awaitSingleOrNull()
    }

    /**
     * Существует ли сущность с [id] в БД
     * @param id Идентификатор access токена
     */
    suspend fun existsBySessionId(sessionId: String): Boolean = withContext(Dispatchers.IO) {
        reactiveRedisTemplate.opsForValue().get(sessionId).awaitSingleOrNull() != null
    }
}