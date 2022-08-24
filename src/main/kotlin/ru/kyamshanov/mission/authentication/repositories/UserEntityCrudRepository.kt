package ru.kyamshanov.mission.authentication.repositories

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import ru.kyamshanov.mission.authentication.entities.UserEntity

/**
 * CRUD репозиторий для хранения сущнстей пользователя
 */
internal interface UserEntityCrudRepository : CoroutineCrudRepository<UserEntity, String> {
    suspend fun existsByLogin(id: String): Boolean

    suspend fun findByLogin(login: String): UserEntity?
}