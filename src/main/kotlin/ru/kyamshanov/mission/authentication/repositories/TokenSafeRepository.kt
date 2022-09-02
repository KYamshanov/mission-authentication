package ru.kyamshanov.mission.authentication.repositories

import kotlinx.coroutines.flow.last
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.kyamshanov.mission.authentication.components.GetCurrentLocalDateTimeUseCase
import ru.kyamshanov.mission.authentication.entities.TokenEntity

/**
 * Безопасный репозиторий для хранения сущнстей сессионных токен
 * @property tokenCrudRepository CRUD репозиторий для хранения сущнстей сессионных токен
 * @property getCurrentLocalDateTimeUseCase UseCase для получения даты без тайм зоны
 */
@Repository
internal class TokenSafeRepository @Autowired constructor(
    private val tokenCrudRepository: TokenCrudRepository,
    private val getCurrentLocalDateTimeUseCase: GetCurrentLocalDateTimeUseCase
) {
    suspend fun findById(id: String): TokenEntity? = tokenCrudRepository.findById(id)

    suspend fun save(token: TokenEntity): TokenEntity = tokenCrudRepository.save(token)

    suspend fun deleteExpiredTokens() = tokenCrudRepository.deleteExpiredTokens(getCurrentLocalDateTimeUseCase())

    @Transactional
    suspend fun saveTokens(vararg tokenStatus: TokenEntity) {
        tokenCrudRepository.saveAll(tokenStatus.toList()).last()
    }
}