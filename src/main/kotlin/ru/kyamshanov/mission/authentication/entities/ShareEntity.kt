package ru.kyamshanov.mission.authentication.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

/**
 * Сущность таблицы - сессионные токены
 * @property userId Id юзера
 * @property sessionId Сессия из которой происходит внешняя аутентификация
 * @property createdAt Дата создания сущности
 * @property expiresAt Дата истечения срока действия токена
 * @property status Статус share-auth токена
 * @property givenId Id сущности
 */
@Table("auth_share")
internal data class ShareEntity(
    @Column("user_id")
    val userId: String,
    @Column("session_id")
    val sessionId: String,
    @Column("created_at")
    val createdAt: Instant,
    @Column("expires_at")
    val expiresAt: Instant,
    @Column("status")
    val status: TokenStatus,
    @Id
    @Column("id")
    private val givenId: String? = null
) : AbstractEntity(givenId)