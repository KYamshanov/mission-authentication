package ru.kyamshanov.mission.authentication.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.kyamshanov.mission.authentication.GlobalConstants.KEY_SHARE_TOKEN_LIFE_TIME
import ru.kyamshanov.mission.authentication.components.ExpireVerificationValidator
import ru.kyamshanov.mission.authentication.components.GetCurrentInstantUseCase
import ru.kyamshanov.mission.authentication.entities.ShareEntity
import ru.kyamshanov.mission.authentication.entities.TokenStatus
import ru.kyamshanov.mission.authentication.errors.TokenNotFoundException
import ru.kyamshanov.mission.authentication.errors.TokenStatusException
import ru.kyamshanov.mission.authentication.errors.UserNotFoundException
import ru.kyamshanov.mission.authentication.models.JsonMap
import ru.kyamshanov.mission.authentication.models.JwtPair
import ru.kyamshanov.mission.authentication.propcessors.JwtProcessor
import ru.kyamshanov.mission.authentication.repositories.ShareEntityCrudRepository
import ru.kyamshanov.mission.authentication.repositories.UserEntityCrudRepository
import java.time.Instant

/**
 * Сервис для внешней аутентификации по токену
 */
internal interface ShareAuthenticationService {

    /**
     * Создать share-auth токен для внешней аутентификации по токену
     * @return Токен
     */
    suspend fun createShareAuthToken(refreshToken: String): String

    /**
     * Аутентифицировать пользователя через share_auth токен
     * @param shareToken Токен для аутентификации
     * @param userInfo Информация о пользователе
     * @return Пара Jwt токенов access/refresh от новой сессии
     */
    suspend fun login(shareToken: String, userInfo: JsonMap): JwtPair
}

/**
 * Реализация [RegistrationService]
 * @property jwtProcessor Обработчик JWT
 * @property shareEntityCrudRepository CRUD репозиторий для хранения сущнстей share-auth токенов
 * @property userEntityCrudRepository CRUD репозиторий для хранения сущнстей пользователя
 * @property getCurrentInstantUseCase Получение текущей даты в формате [Instant]
 * @property shareTokenLifeTime Время жизни auth-share токена в сек.
 * @property expireVerificationValidator Средство проверки истечения срока действия
 */
@Service
internal class ShareAuthenticationServiceImpl @Autowired constructor(
    private val jwtProcessor: JwtProcessor,
    private val shareEntityCrudRepository: ShareEntityCrudRepository,
    private val userEntityCrudRepository: UserEntityCrudRepository,
    private val getCurrentInstantUseCase: GetCurrentInstantUseCase,
    @Value("\${$KEY_SHARE_TOKEN_LIFE_TIME}") private val shareTokenLifeTime: Long,
    private val expireVerificationValidator: ExpireVerificationValidator
) : ShareAuthenticationService {

    override suspend fun createShareAuthToken(refreshToken: String): String {
        val shareEntity = jwtProcessor.verifyRefreshToken(refreshToken).let {
            ShareEntity(
                userId = it.userId,
                sessionId = it.tokenId,
                createdAt = getCurrentInstantUseCase(),
                expiresAt = getCurrentInstantUseCase().plusSeconds(shareTokenLifeTime),
                status = TokenStatus.ACTIVE
            )
        }
        return shareEntityCrudRepository.save(shareEntity).id
    }

    override suspend fun login(shareToken: String, userInfo: JsonMap): JwtPair {
        val shareEntity = (shareEntityCrudRepository.findById(shareToken)
            ?: throw TokenNotFoundException("share-auth token $shareToken not found")).also {
            if (it.status != TokenStatus.ACTIVE) throw TokenStatusException("Token`s status is not ACTIVE. Status for $shareToken is ${it.status}")
            expireVerificationValidator(it.expiresAt)
            jwtProcessor.verifySessionById(it.sessionId)
        }
        val userEntity = userEntityCrudRepository.findById(shareEntity.userId)
            ?: throw UserNotFoundException("User with id = ${shareEntity.userId} not found")

        val shareAuthUserInfo = HashMap(userInfo.map).apply {
            put(
                KEY_SHARE_AUTH_SESSION_MARK,
                mapOf(KEY_SOURCE_TOKEN_ID to shareEntity.id, KEY_SOURCE_SESSION to shareEntity.sessionId)
            )
        }.let { JsonMap(it) }

        val createdSession = jwtProcessor.createSession(
            userId = userEntity.id, userLogin = userEntity.login, userInfo = shareAuthUserInfo
        )
        shareEntityCrudRepository.save(shareEntity.copy(status = TokenStatus.INVALID))
        return createdSession
    }

    companion object {
        private const val KEY_SHARE_AUTH_SESSION_MARK = "share_auth"
        private const val KEY_SOURCE_TOKEN_ID = "source_token_id"
        private const val KEY_SOURCE_SESSION = "source_session"
    }
}