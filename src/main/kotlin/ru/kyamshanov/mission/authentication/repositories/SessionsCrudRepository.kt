package ru.kyamshanov.mission.authentication.repositories

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import ru.kyamshanov.mission.authentication.entities.SessionEntity

/**
 * CRUD репозиторий для хранения сущнстей сессионных токен
 */
internal interface SessionsCrudRepository : CoroutineCrudRepository<SessionEntity, String> {

    suspend fun findByRefreshId(refreshId: String): SessionEntity?
}