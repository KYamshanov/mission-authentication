package ru.kyamshanov.mission.authentication.repositories

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import ru.kyamshanov.mission.authentication.entities.EntityStatus
import ru.kyamshanov.mission.authentication.entities.SessionEntity
import java.time.Instant

/**
 * CRUD репозиторий для сущнстей сессий
 */
internal interface SessionsCrudRepository : CoroutineCrudRepository<SessionEntity, String> {

    /**
     * Изменить статус сессии
     * @param sessionId Идентификатор сессии
     * @param status Новый статус сессии
     * @param updatedAt Дата изменения сущности
     * @return [Flow] Обновленных сущностей
     */
    @Query("UPDATE mission.public.auth_sessions SET status = :status, updated_at = :updatedAt WHERE id = :sessionId RETURNING *")
    fun setSessionStatus(sessionId: String, status: EntityStatus, updatedAt: Instant): Flow<SessionEntity>

    /**
     * Найти все сессии пользователя
     * @param userId Идентификатор пользователя
     * @return [Flow] Сущностей сессий
     */
    fun findAllByUserId(userId: String): Flow<SessionEntity>
}