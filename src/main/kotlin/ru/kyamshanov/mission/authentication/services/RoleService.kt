package ru.kyamshanov.mission.authentication.services

import kotlinx.coroutines.flow.toList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.kyamshanov.mission.authentication.models.User
import ru.kyamshanov.mission.authentication.models.UserRole
import ru.kyamshanov.mission.authentication.repositories.UserRolesRepository

/**
 * Сервис ролей
 */
internal interface RoleService {

    /**
     * Получить роли пользователя
     * @param user Юзер
     * @return Список ролей
     */
    suspend fun getUserRoles(user: User): List<UserRole>

    /**
     * Изменить роли пользователя
     * @param user Пользователь
     * @param roles Новые роли пользователя
     * @return Пользователя с измененными ролями
     */
    suspend fun setUserRoles(user: User, roles: List<UserRole>): User

}

@Service
private class RoleServiceImpl @Autowired constructor(
    private val rolesRepository: UserRolesRepository
) : RoleService {

    override suspend fun getUserRoles(user: User): List<UserRole> {
        requireNotNull(user.id) { "External userId required for passed user" }
        return rolesRepository.getUserRoles(user.id).toList().map { UserRole.valueOf(it.roleName) }
    }

    override suspend fun setUserRoles(user: User, roles: List<UserRole>): User {
        requireNotNull(user.id) { "Id of passed user required for set roles" }
        rolesRepository.setUserRoles(user.id, roles)
        return user.apply {
            credentials.roles = roles
        }
    }
}

