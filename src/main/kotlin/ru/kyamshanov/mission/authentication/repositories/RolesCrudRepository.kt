package ru.kyamshanov.mission.authentication.repositories

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import ru.kyamshanov.mission.authentication.entities.RoleEntity

/**
 * CRUD репозиторий для хранения сущнстей ролей
 */
@Repository
internal interface RolesCrudRepository : CoroutineCrudRepository<RoleEntity, Int>