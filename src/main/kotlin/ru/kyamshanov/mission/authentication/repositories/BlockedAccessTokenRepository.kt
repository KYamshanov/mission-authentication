package ru.kyamshanov.mission.authentication.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import ru.kyamshanov.mission.authentication.entities.BlockAccessTokenEntity

/**
 * Репозиторий блокировки access токена
 * @property reactiveRedisTemplate Система для реактивного доступа к данным БД Redis
 */
@Repository
internal class BlockedAccessTokenRepository @Autowired constructor(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>
) {

    /**
     * Сохранить блокировку access токена
     * @param entity Сущность блокировки
     */
    suspend fun save(entity: BlockAccessTokenEntity) = withContext(Dispatchers.IO) {
        reactiveRedisTemplate.opsForHash<String, String>().putAll(
            entity.id, mapOf(
                KEY_JWT_ID to entity.id,
                KEY_EXPIRES_AT to entity.expiresAt.toString()
            )
        ).awaitSingleOrNull()
    }

    /**
     * Существует ли сущность с [id] в БД
     * @param id Идентификатор access токена
     */
    suspend fun existsById(id: String): Boolean = withContext(Dispatchers.IO) {
        reactiveRedisTemplate.opsForHash<String, String>().hasKey(id, KEY_JWT_ID).awaitSingleOrNull() ?: false
    }

    private companion object {
        const val KEY_JWT_ID = "id"
        const val KEY_EXPIRES_AT = "expiresAt"
    }
}