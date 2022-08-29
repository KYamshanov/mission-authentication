package ru.kyamshanov.mission.authentication.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.kyamshanov.mission.authentication.GlobalConstants.KEY_SHARE_TOKEN_LIFE_TIME
import ru.kyamshanov.mission.authentication.components.GetCurrentLocalDateTimeUseCase
import ru.kyamshanov.mission.authentication.entities.ShareEntity
import ru.kyamshanov.mission.authentication.entities.TokenStatus
import ru.kyamshanov.mission.authentication.propcessors.JwtProcessor
import ru.kyamshanov.mission.authentication.repositories.ShareEntityCrudRepository
import java.time.LocalDateTime

/**
 * Сервис для внешней аутентификации по токену
 */
internal interface ShareAuthenticationService {

    /**
     * Создать share-auth токен для внешней аутентификации по токену
     * @return Токен
     */
    suspend fun createShareAuthToken(refreshToken: String): String
}

/**
 * Реализация [RegistrationService]
 * @property jwtProcessor Обработчик JWT
 * @property shareEntityCrudRepository CRUD репозиторий для хранения сущнстей share-auth токенов
 * @property getCurrentLocalDateTimeUseCase Получение текущей даты в формате [LocalDateTime]
 * @property shareTokenLifeTime Время жизни auth-share токена в сек.
 */
@Service
internal class ShareAuthenticationServiceImpl @Autowired constructor(
    private val jwtProcessor: JwtProcessor,
    private val shareEntityCrudRepository: ShareEntityCrudRepository,
    private val getCurrentLocalDateTimeUseCase: GetCurrentLocalDateTimeUseCase,
    @Value("\${$KEY_SHARE_TOKEN_LIFE_TIME}")
    private val shareTokenLifeTime: Long
) : ShareAuthenticationService {

    override suspend fun createShareAuthToken(refreshToken: String): String {
        val shareEntity = jwtProcessor.verifyRefreshToken(refreshToken).let {
            ShareEntity(
                userId = it.userId,
                sessionId = it.tokenId,
                createdAt = getCurrentLocalDateTimeUseCase(),
                expiresAt = getCurrentLocalDateTimeUseCase().plusSeconds(shareTokenLifeTime),
                status = TokenStatus.ACTIVE
            )
        }
        return shareEntityCrudRepository.save(shareEntity).id
    }
}