package ru.kyamshanov.mission.authentication.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

/**
 * Сущность таблицы - сессионные токены
 * @property sessionId Идентификатор сессии (устаналивается один раз при логине)
 * @property refreshId Идентификатор рефреш токена сессии (устаналивается новый каждый раз при refresh)
 * @property userId Id юзера
 * @property createdAt Дата создания сущности
 * @property expiresAt Дата истечения срока действия рефреш токена
 * @property status Статус сессии
 * @property sessionInfo Информация юзера создавшего сессиию
 * @property givenId Id сущности
 */
@Table("auth_sessions")
internal data class SessionEntity(
    @Column("user_id")
    val userId: String,
    @Column("created_at")
    val createdAt: Instant,
    @Column("updated_at")
    val updatedAt: Instant,
    @Column("status")
    val status: EntityStatus,
    @Id
    @Column("id")
    private val givenId: String? = null
) : AbstractEntity(givenId)