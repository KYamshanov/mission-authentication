package ru.kyamshanov.mission.authentication.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import ru.kyamshanov.mission.authentication.models.JsonMap
import java.time.Instant

/**
 * Сущность таблицы - сессионные токены
 * @property sessionId Идентификатор сессии
 * @property refreshId Идентификатор рефреш токена сессии (устаналивается новый каждый раз при refresh)
 * @property createdAt Дата создания записи
 * @property updatedAt Последняя дата изменения записи
 * @property expiresAt Дата истечения срока действия рефреш токена
 * @property status Статус токена
 * @property userInfo Информация юзера создавшего сессиию
 * @property givenId Id сущности
 */
@Table("auth_session_tokens")
internal data class SessionTokenEntity(
    @Column("session_id")
    val sessionId: String,
    @Column("refresh_id")
    val refreshId: String,
    @Column("created_at")
    val createdAt: Instant,
    @Column("updated_at")
    val updatedAt: Instant,
    @Column("expires_at")
    val expiresAt: Instant,
    @Column("status")
    val status: EntityStatus,
    @Column("info")
    val userInfo: JsonMap,
    @Id
    @Column("id")
    private val givenId: String? = null
) : AbstractEntity(givenId)