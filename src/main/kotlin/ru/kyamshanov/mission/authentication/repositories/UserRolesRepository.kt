package ru.kyamshanov.mission.authentication.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.toList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.kyamshanov.mission.authentication.entities.RoleEntity
import ru.kyamshanov.mission.authentication.entities.UserRoleEntity
import ru.kyamshanov.mission.authentication.models.UserRole

/**
 * Репозиторий ролей пользователя
 */
internal interface UserRolesRepository {

    /**
     * Получить роли пользователя
     * @param userId Id пользователя
     * @return [Flow]<[RoleEntity]>
     */
    fun getUserRoles(userId: String): Flow<RoleEntity>

    /**
     * Изменить роли пользователя
     * @param userId Идентификатор юзера
     * @param roles Новый список ролей
     */
    suspend fun setUserRoles(userId: String, roles: List<UserRole>)
}

/**
 * Реализация [UserRolesRepository]
 * @property [native] Репозиторий ролей используеющий нативные запросы к БД
 * @property [userRoleCrudRepository] CRUD репозиторий для хранения сущнстей ролей
 */

@Repository
internal class UserRolesRepositoryImpl @Autowired constructor(
    private val native: UserRolesNativeQueryRepository,
    private val userRoleCrudRepository: UserRoleCrudRepository
) : UserRolesRepository {
    override fun getUserRoles(userId: String): Flow<RoleEntity> =
        native.getUserRoles(userId)

    @Transactional
    override suspend fun setUserRoles(userId: String, roles: List<UserRole>) {
        userRoleCrudRepository.deleteAllByUserId(userId)
        val rolesIds = native.getRolesByNames(roles.map { it.name })
            .toList().mapNotNull { it.id }
        val userRoleEntities = rolesIds.map { roleId ->
            UserRoleEntity(
                userId = userId,
                roleId = roleId
            )
        }
        assert(userRoleCrudRepository.saveAll(userRoleEntities).count() == roles.size) { "Not all roles saved" }
    }
}
