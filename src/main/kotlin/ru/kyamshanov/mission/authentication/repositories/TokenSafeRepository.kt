package ru.kyamshanov.mission.authentication.repositories

import kotlinx.coroutines.flow.last
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.kyamshanov.mission.authentication.entities.TokenEntity

/**
 * Безопасный репозиторий для хранения сущнстей сессионных токен
 * @property tokenCrudRepository CRUD репозиторий для хранения сущнстей сессионных токен
 */
@Repository
internal class TokenSafeRepository @Autowired constructor(
    private val tokenCrudRepository: TokenCrudRepository
) {
    suspend fun findById(id: String): TokenEntity? =
        tokenCrudRepository.findById(id)

    suspend fun save(token: TokenEntity): TokenEntity =
        tokenCrudRepository.save(token)

    @Transactional
    suspend fun saveTokens(vararg tokenStatus: TokenEntity) {
        tokenCrudRepository.saveAll(tokenStatus.toList()).last()
    }
}