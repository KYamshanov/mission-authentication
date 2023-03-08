package ru.kyamshanov.mission.authentication.repositories

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import ru.kyamshanov.mission.authentication.entities.UserEntity

/**
 * CRUD репозиторий для хранения сущнстей пользователя
 */
@Repository
internal interface UserEntityCrudRepository : CoroutineCrudRepository<UserEntity, String> {

    /**
     * Найти пользователя по логину
     * @param login Логин пользователя
     * @return Если пользователя существует - [UserEntity], инче - null
     */
    suspend fun findByLogin(login: String): UserEntity?
}