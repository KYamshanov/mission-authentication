package ru.kyamshanov.mission.authentication.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
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
@Table("auth_session_tokens")
internal data class SessionTokenEntity(
    @Column("session_id")
    val sessionId: String,
    @Column("created_at")
    val createdAt: Instant,
    @Column("expires_at")
    val expiresAt: Instant,
    @Column("info")
    val userInfo: JsonMap,
    @Id
    @Column("id")
    private val givenId: String? = null
) : AbstractEntity(givenId)