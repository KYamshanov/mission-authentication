package ru.kyamshanov.mission.authentication.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

/**
 * Сущность таблицы - пользователи
 * @property login Имя пользователя
 * @property password Пароль
 * @property externalId Внешний идентификатор пользователя
 * @property givenId Id сущности
 */
@Table("auth_users")
internal data class UserEntity(
    val login: String,
    val password: CharSequence,
    @Column("external_id")
    val externalId: String,
    @Id
    @Column("id")
    private val givenId: String? = null
) : AbstractEntity(givenId)


