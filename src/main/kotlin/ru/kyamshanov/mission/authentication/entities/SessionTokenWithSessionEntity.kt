package ru.kyamshanov.mission.authentication.entities

import org.springframework.data.relational.core.mapping.Column
import ru.kyamshanov.mission.authentication.models.JsonMap
import java.time.Instant

/**
 * Сущность таблицы - сессионные токены
 * @property sessionId Идентификатор сессии (устаналивается один раз при логине)
 * @property refreshId Идентификатор рефреш токена сессии (устаналивается новый каждый раз при refresh)
 * @property userId Id юзера
 * @property createdAt Дата создания сущности
 * @property expiresAt Дата истечения срока действия рефреш токена
 * @property status Статус сессии
 * @property userInfo Информация юзера создавшего сессиию
 * @property givenId Id сущности
 */
internal data class SessionTokenWithSessionEntity(
    @Column("session_id")
    val sessionId: String,
    @Column("user_id")
    val userId: String,
    @Column("sessions_created_at")
    val sessionCreatedAt: Instant,
    @Column("session_updated_at")
    val sessionUpdatedAt: Instant,
    @Column("status")
    val status: EntityStatus,
    @Column("token_updated_at")
    val tokenUpdatedAt: Instant,
    @Column("refresh_id")
    val refreshId: String,
    @Column("token_created_at")
    val tokenCreatedAt: Instant,
    @Column("expires_at")
    val expiresAt: Instant,
    @Column("info")
    val userInfo: JsonMap,
    @Column("token_id")
    val tokenId: String
)

internal fun SessionTokenWithSessionEntity.toSessionEntity(): SessionEntity = SessionEntity(
    userId = userId,
    createdAt = sessionCreatedAt,
    updatedAt = sessionUpdatedAt,
    status = status,
    givenId = sessionId
)

internal fun SessionTokenWithSessionEntity.toSessionTokenEntity(): SessionTokenEntity = SessionTokenEntity(
    sessionId = sessionId,
    refreshId = refreshId,
    createdAt = tokenCreatedAt,
    updatedAt = tokenUpdatedAt,
    expiresAt = expiresAt,
    userInfo = userInfo,
    givenId = tokenId
)