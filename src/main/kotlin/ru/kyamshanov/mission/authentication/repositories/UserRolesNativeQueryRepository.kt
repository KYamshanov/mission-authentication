package ru.kyamshanov.mission.authentication.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import ru.kyamshanov.mission.authentication.entities.RoleEntity

/**
 * Репозиторий ролей используеющий нативные запросы к БД
 * @property r2dbcEntityTemplate Реактивное средство для запросов к БД
 * @property converter Средство для конвертирования записей из БД в необходимую модель Entity
 */

@Repository
internal class UserRolesNativeQueryRepository @Autowired constructor(
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
    private val converter: MappingR2dbcConverter
) {

    /**
     * Получить роли пользоваетля
     * @param externalUserId Внешний ИД юзера
     * @return [Flow]<[RoleEntity]>
     */
    fun getUserRoles(externalUserId: String): Flow<RoleEntity> =
        r2dbcEntityTemplate.databaseClient.sql(
            "  SELECT roles.*\n" +
                    "FROM auth_users users\n" +
                    "         RIGHT JOIN user_role ON users.id = user_role.user_id\n" +
                    "         RIGHT JOIN roles ON user_role.role_id = roles.id\n" +
                    "WHERE users.external_id = '$externalUserId'"
        )
            .map { t, u -> converter.read(RoleEntity::class.java, t, u) }
            .all().asFlow()

    /**
     * Получить сущности ролей по названию
     * @param roleNames Список названий ролей
     * @return [Flow]<[RoleEntity]>
     */
    fun getRolesByNames(roleNames: List<String>): Flow<RoleEntity> {
        val arrayOfName = roleNames.joinToString(", ") { "'$it'" }

        return r2dbcEntityTemplate.databaseClient.sql(
            "SELECT * FROM roles WHERE role_name = ANY(ARRAY[$arrayOfName])"
        )
            .map { t, u -> converter.read(RoleEntity::class.java, t, u) }
            .all().asFlow()
    }
}
