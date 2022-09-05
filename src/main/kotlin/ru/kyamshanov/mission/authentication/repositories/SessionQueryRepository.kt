package ru.kyamshanov.mission.authentication.repositories

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Repository
import ru.kyamshanov.mission.authentication.entities.SessionTokenWithSessionEntity

/**
 * CRUD репозиторий для хранения сущнстей сессионных токен
 */
@Repository
internal class SessionQueryRepository @Autowired constructor(
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
    private val converter: MappingR2dbcConverter
) {

    suspend fun findSessionByRefreshId(refreshId: String): SessionTokenWithSessionEntity =
        r2dbcEntityTemplate.databaseClient.sql("SELECT sessions.id as session_id, sessions.user_id as user_id, sessions.created_at as sessions_created_at, sessions.updated_at as session_updated_at, sessions.status, tokens.updated_at as token_updated_at, tokens.refresh_id,tokens.created_at as token_created_at, tokens.expires_at, tokens.info, tokens.id as token_id FROM auth_session_tokens tokens LEFT JOIN auth_sessions AS sessions ON sessions.id = tokens.session_id WHERE refresh_id = '$refreshId' LIMIT 1")
            .map { t, u ->
                converter.read(SessionTokenWithSessionEntity::class.java, t, u)
            }
            .first().awaitSingle()


    /**
     * Удалить токены с истекшим сроком действия
     * @param since Минимальная дата истечения скрока дейтсивя
     */

/*

 /*   SessionTokenWithSessionEntity(
                    userId = requireNotNull(it["user_id", String::class.java]),
                    sessionCreatedAt = requireNotNull(it["sessions_created_at", Instant::class.java]),
                    sessionUpdatedAt = requireNotNull(it["session_updated_at", Instant::class.java]),
                    tokenUpdatedAt = requireNotNull(it["token_updated_at", Instant::class.java]),
                    status = requireNotNull(it["status", EntityStatus::class.java]),
                    sessionId = requireNotNull(it["session_id", String::class.java]),
                    refreshId = requireNotNull(it["refresh_id", String::class.java]),
                    tokenCreatedAt = requireNotNull(it["token_created_at", Instant::class.java]),
                    expiresAt = requireNotNull(it["expires_at", Instant::class.java]),
                    userInfo = requireNotNull(it["info", JsonMap::class.java]),
                    tokenId = requireNotNull(it["token_id", String::class.java])
                )*/

    */
    /**
     * Удалить токены с истекшим сроком действия
     * @param expiresAt Минимальная дата истечения скрока дейтсивя
     *//*
    @Query("DELETE FROM mission.public.auth_session_tokens WHERE expires_at <= :date")
    abstract suspend fun deleteExpiredTokens(expiresAt: Instant)

    @Query(
        "SELECT * FROM auth_session_tokens tokens LEFT JOIN auth_sessions sessions ON sessions.id = tokens.session_id WHERE refresh_id = :refreshId LIMIT 1"
    )
    abstract suspend fun findSessionByRefreshId(refreshId: String): SessionTokenWithSessionEntity?*/
}