package ru.kyamshanov.mission.authentication.entities

import ru.kyamshanov.mission.authentication.models.User
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

/**
 * Сущность таблицы - пользователи
 * @property id - [UUID] идентификатор пользователя
 * @property login Имя пользователя
 * @property password Пароль
 */
@Entity
@Table(name = "users")
internal data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    @Column(name = "login")
    val login: String? = null,
    @Column(name = "password")
    val password: String? = null
)

/**
 * Конвертировать модель пользователя в предствление таблицы
 * [User] -> [UserEntity]
 */
internal fun User.toEntity(): UserEntity {
    return UserEntity(login = login, password = password)
}

/**
 * Конвертировать сущность пользователя таблицы в модель
 * [UserEntity] -> [User]
 */
internal fun UserEntity.toModel(): User {
    requireNotNull(login)
    requireNotNull(password)

    return User(login, password)
}