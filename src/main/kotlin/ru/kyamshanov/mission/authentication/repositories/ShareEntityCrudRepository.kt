package ru.kyamshanov.mission.authentication.repositories

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import ru.kyamshanov.mission.authentication.entities.ShareEntity

/**
 * CRUD репозиторий для хранения сущнстей share-auth токенов
 */
@Repository
internal interface ShareEntityCrudRepository : CoroutineCrudRepository<ShareEntity, String>