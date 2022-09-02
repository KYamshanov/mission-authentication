package ru.kyamshanov.mission.authentication.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

/**
 * Сущность таблицы - сессия пользователя
 * @property userId Id юзера
 * @property createdAt Дата создания сущности
 * @property updatedAt Дата изменения записи
 * @property status Статус сессии
 * @property givenId Id сущности / Идентификатор сессии
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