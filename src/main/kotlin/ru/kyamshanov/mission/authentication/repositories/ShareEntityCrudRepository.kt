package ru.kyamshanov.mission.authentication.repositories

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import ru.kyamshanov.mission.authentication.entities.ShareEntity

/**
 * CRUD репозиторий для хранения сущнстей share-auth токенов
 */
internal interface ShareEntityCrudRepository : CoroutineCrudRepository<ShareEntity, String>