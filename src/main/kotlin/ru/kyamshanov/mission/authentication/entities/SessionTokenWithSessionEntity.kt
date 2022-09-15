package ru.kyamshanov.mission.authentication.entities

import org.springframework.data.relational.core.mapping.Column
import ru.kyamshanov.mission.authentication.models.JsonMap
import java.time.Instant

/**
 * Сущность Сессиного токена + информация о сессии
 * @property sessionId Идентификатор сессии
 * @property userId Идентификатор пользователя сессии
 * @property sessionCreatedAt Дата создания сессионной записи
 * @property sessionUpdatedAt Дата изменения сессионной записи
 * @property sessionStatus Статус сессии
 * @property tokenUpdatedAt Дата обнвления записи сессионного токена
 * @property refreshId Идентификатор рефреш токена
 * @property tokenCreatedAt Дата создания записи сессионного токена
 * @property expiresAt Дата прекращения действия сессионного токена
 * @property userInfo Информация о пользователе
 * @property tokenId Идентификатор записи сессионного токена
 * @property tokenStatus Статус сессонного токена
 */
internal data class SessionTokenWithSessionEntity(
    @Column("session_id") val sessionId: String,
    @Column("user_id") val userId: String,
    @Column("sessions_created_at") val sessionCreatedAt: Instant,
    @Column("session_updated_at") val sessionUpdatedAt: Instant,
    @Column("session_status") val sessionStatus: EntityStatus,
    @Column("token_updated_at") val tokenUpdatedAt: Instant,
    @Column("refresh_id") val refreshId: String,
    @Column("token_created_at") val tokenCreatedAt: Instant,
    @Column("expires_at") val expiresAt: Instant,
    @Column("info") val userInfo: JsonMap,
    @Column("token_id") val tokenId: String,
    @Column("token_status") val tokenStatus: EntityStatus,
)

/**
 * Конвертировать [SessionTokenWithSessionEntity] -> [SessionEntity]
 */
internal fun SessionTokenWithSessionEntity.toSessionEntity(): SessionEntity = SessionEntity(
    userId = userId,
    createdAt = sessionCreatedAt,
    updatedAt = sessionUpdatedAt,
    status = sessionStatus,
    givenId = sessionId
)

/**
 * Конвертировать [SessionTokenWithSessionEntity] -> [SessionTokenEntity]
 */
internal fun SessionTokenWithSessionEntity.toSessionTokenEntity(): SessionTokenEntity = SessionTokenEntity(
    sessionId = sessionId,
    refreshId = refreshId,
    createdAt = tokenCreatedAt,
    updatedAt = tokenUpdatedAt,
    expiresAt = expiresAt,
    userInfo = userInfo,
    givenId = tokenId,
    status = tokenStatus
)