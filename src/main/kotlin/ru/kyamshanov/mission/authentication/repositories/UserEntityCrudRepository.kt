package ru.kyamshanov.mission.authentication.repositories

import org.springframework.data.repository.CrudRepository
import ru.kyamshanov.mission.authentication.entities.UserEntity
import java.util.UUID

/**
 * CRUD репозиторий для хранения сущнстей пользователя
 */
internal interface UserEntityCrudRepository : CrudRepository<UserEntity, UUID> {

}