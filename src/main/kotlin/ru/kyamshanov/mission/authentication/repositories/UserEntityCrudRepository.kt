package ru.kyamshanov.mission.authentication.repositories

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import ru.kyamshanov.mission.authentication.entities.UserEntity

/**
 * CRUD репозиторий для хранения сущнстей пользователя
 */
internal interface UserEntityCrudRepository : CoroutineCrudRepository<UserEntity, String> {

    /**
     * Проверить существует ли пользователь по логину
     * @param login Логин пользователя
     */
    suspend fun existsByLogin(login: String): Boolean

    /**
     * Найти пользователя по логину
     * @param login Логин пользователя
     * @return Если пользователя существует - [UserEntity], инче - null
     */
    suspend fun findByLogin(login: String): UserEntity?

    /**
     * Найти пользователя по внешнему Id
     * @param externalId Внешний идентификатор пользователя
     * @return [UserEntity]
     */
    suspend fun findByExternalId(externalId: String): UserEntity?
}