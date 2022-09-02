package ru.kyamshanov.mission.authentication.repositories

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import ru.kyamshanov.mission.authentication.entities.TokenEntity
import java.time.LocalDateTime

/**
 * CRUD репозиторий для хранения сущнстей сессионных токен
 */
internal interface TokenCrudRepository : CoroutineCrudRepository<TokenEntity, String> {


    /**
     * Удалить токены с истекшим сроком действия
     * @param expiresAt Минимальная дата истечения скрока дейтсивя
     */
    @Query("DELETE FROM mission.public.auth_tokens WHERE refresh_expires_at <= :date")
    suspend fun deleteExpiredTokens(expiresAt: LocalDateTime)
}