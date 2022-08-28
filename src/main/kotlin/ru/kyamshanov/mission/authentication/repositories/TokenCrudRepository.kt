package ru.kyamshanov.mission.authentication.repositories

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import ru.kyamshanov.mission.authentication.entities.TokenEntity

/**
 * CRUD репозиторий для хранения сущнстей сессионных токен
 */
internal interface TokenCrudRepository : CoroutineCrudRepository<TokenEntity, String>