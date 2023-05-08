package ru.kyamshanov.mission.authentication.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

/**
 * Сущность таблицы - роль
 * @property id Идентификатор роли
 * @property roleName Название роли
 */
@Table("roles")
internal data class RoleEntity(
    @Id
    @Column("id")
    val id: Int? = null,
        @Column("role_name")
    val roleName: String
)