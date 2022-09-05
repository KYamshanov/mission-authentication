package ru.kyamshanov.mission.authentication.repositories

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import ru.kyamshanov.mission.authentication.entities.SessionTokenEntity
import java.time.Instant

/**
 * CRUD репозиторий для хранения сущнстей сессионных токен
 */
internal interface SessionTokenCrudRepository : CoroutineCrudRepository<SessionTokenEntity, String> {

    @Query("DELETE FROM mission.public.auth_session_tokens WHERE expires_at <= :sinceExpiresAt")
    suspend fun deleteOlderTokens(sinceExpiresAt: Instant): Int
}