package ru.kyamshanov.mission.authentication.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

/**
 * Сущность таблицы - роль-пользователь
 * @property id Идентификатор биндинга
 * @property userId Идентификатор юзера
 * @property roleId Идентификатор роли
 */
@Table("user_role")
internal data class UserRoleEntity(
    @Id
    @Column("id")
    private val id: Int? = null,
    @Column("user_id")
    val userId: String,
    @Column("role_id")
    val roleId: Int
)