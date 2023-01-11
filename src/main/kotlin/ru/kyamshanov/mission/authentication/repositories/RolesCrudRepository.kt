package ru.kyamshanov.mission.authentication.repositories

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import ru.kyamshanov.mission.authentication.entities.RoleEntity

/**
 * CRUD репозиторий для хранения сущнстей ролей
 */
internal interface RolesCrudRepository : CoroutineCrudRepository<RoleEntity, Int>