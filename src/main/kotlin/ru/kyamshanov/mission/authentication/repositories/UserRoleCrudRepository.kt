package ru.kyamshanov.mission.authentication.repositories

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import ru.kyamshanov.mission.authentication.entities.UserRoleEntity

/**
 * CRUD репозиторий для хранения сущнстей ролей
 */
internal interface UserRoleCrudRepository : CoroutineCrudRepository<UserRoleEntity, Int> {

    /**
     * Удалить роли пользователя
     * @param userId Идентификатор юзера
     */
    suspend fun deleteAllByUserId(userId: String)
}