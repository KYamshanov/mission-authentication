package ru.kyamshanov.mission.authentication.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import ru.kyamshanov.mission.authentication.models.JsonMap
import java.time.LocalDateTime

/**
 * Сущность таблицы - сессионные токены
 * @property userId Id юзера
 * @property createdAt Дата создания сущности
 * @property refreshExpiresAt Дата истечения срока действия рефреш токена
 * @property status Статус сессии
 * @property sessionInfo Информация юзера создавшего сессиию
 * @property givenId Id сущности
 */
@Table("auth_tokens")
internal data class TokenEntity(
    @Column("user_id")
    val userId: String,
    @Column("created_at")
    val createdAt: LocalDateTime,
    @Column("refresh_expires_at")
    val refreshExpiresAt: LocalDateTime,
    @Column("status")
    val status: TokenStatus,
    @Column("info")
    val sessionInfo: JsonMap,
    @Id
    @Column("id")
    private val givenId: String? = null
) : AbstractEntity(givenId) {


    /**
     * Перечесление статусов токена
     */
    enum class TokenStatus {

        /**
         * Активный
         */
        ACTIVE,

        /**
         * Остановленный
         */
        PAUSED,

        /**
         * Недействительный
         */
        INVALID;
    }
}