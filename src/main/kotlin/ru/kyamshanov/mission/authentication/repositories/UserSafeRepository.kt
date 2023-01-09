package ru.kyamshanov.mission.authentication.repositories

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import ru.kyamshanov.mission.authentication.entities.UserEntity

/**
 * Безопасный репозиторий для взаимодействия с пользователем (использует нативные запросы)
 * @property nativeQueryRepository Репозиторий используеющий нативные запросы к БД
 */
@Repository
internal class UserSafeRepository @Autowired constructor(
    private val nativeQueryRepository: NativeQueryRepository
) {

    /**
     * Найти пользователя по refreshId
     * @param refreshId
     */
    suspend fun findUserByRefreshId(refreshId: String): UserEntity? =
        nativeQueryRepository.findUserByRefreshId(refreshId)
}