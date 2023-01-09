package ru.kyamshanov.mission.authentication.repositories

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import ru.kyamshanov.mission.authentication.entities.SessionTokenWithSessionEntity
import ru.kyamshanov.mission.authentication.entities.UserEntity

/**
 * Репозиторий используеющий нативные запросы к БД
 * @property r2dbcEntityTemplate Реактивное средство для запросов к БД
 * @property converter Средство для конвертирования записей из БД в необходимую модель Entity
 */
@Repository
internal class NativeQueryRepository @Autowired constructor(
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
    private val converter: MappingR2dbcConverter
) {

    /**
     * Найти сессию по id refresh токена
     * @param refreshId Идентификатор refresh токена
     * @return Сущность Сессиного токена + информация о сессии
     */
    suspend fun findSessionByRefreshId(refreshId: String): SessionTokenWithSessionEntity? =
        r2dbcEntityTemplate.databaseClient.sql("SELECT sessions.id as session_id, sessions.user_id as user_id, sessions.created_at as sessions_created_at, sessions.updated_at as session_updated_at, sessions.status as session_status, tokens.updated_at as token_updated_at, tokens.refresh_id,tokens.created_at as token_created_at, tokens.expires_at, tokens.info, tokens.id as token_id, tokens.status as token_status FROM auth_session_tokens tokens LEFT JOIN auth_sessions AS sessions ON sessions.id = tokens.session_id WHERE refresh_id = '$refreshId' LIMIT 1")
            .map { t, u ->
                converter.read(SessionTokenWithSessionEntity::class.java, t, u)
            }
            .first().awaitSingleOrNull()

    /**
     * Найти пользователя по refreshId
     * @param refreshId Рефреш токен
     * @return [UserEntity]
     */
    suspend fun findUserByRefreshId(refreshId: String): UserEntity? =
        r2dbcEntityTemplate.databaseClient.sql("SELECT users.id as id, users.login as login, users.password as password, users.external_id as external_id FROM auth_session_tokens tokens LEFT JOIN auth_sessions AS sessions ON sessions.id = tokens.session_id LEFT JOIN auth_users AS users ON sessions.user_id = users.id WHERE refresh_id = '$refreshId' LIMIT 1")
            .map { t, u -> converter.read(UserEntity::class.java, t, u) }
            .first().awaitSingleOrNull()
}