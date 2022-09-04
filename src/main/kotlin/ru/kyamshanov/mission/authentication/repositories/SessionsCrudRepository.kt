package ru.kyamshanov.mission.authentication.repositories

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import ru.kyamshanov.mission.authentication.entities.SessionEntity
import java.time.Instant

/**
 * CRUD репозиторий для хранения сущнстей сессионных токен
 */
internal interface SessionsCrudRepository : CoroutineCrudRepository<SessionEntity, String> {

    suspend fun findByRefreshId(refreshId: String): SessionEntity?

    /**
     * Удалить токены с истекшим сроком действия
     * @param expiresAt Минимальная дата истечения скрока дейтсивя
     */
    @Query("DELETE FROM mission.public.auth_sessions WHERE expires_at <= :date")
    suspend fun deleteExpiredTokens(expiresAt: Instant)
}