package ru.kyamshanov.mission.authentication.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.kyamshanov.mission.authentication.GlobalConstants
import ru.kyamshanov.mission.authentication.components.DecodeJwtTokenUseCase
import ru.kyamshanov.mission.authentication.errors.UserNotFoundException
import ru.kyamshanov.mission.authentication.models.User
import ru.kyamshanov.mission.authentication.models.toModel
import ru.kyamshanov.mission.authentication.repositories.UserSafeRepository

/**
 * Сервис идентификации пользователя
 */
internal interface IdentifyService {

    /**
     * Получить пользователя по рефреш токену
     * @param refreshToken Рефреш токен
     * @return [User]
     *
     * @throws UserNotFoundException Если пользователь не был найден
     */
    suspend fun getUserByRefreshToken(refreshToken: String): User
}

/**
 * Реализация [IdentifyService]
 *
 * @property decodeJwtTokenUseCase UseCase для декодировки jwt токена
 * @property userSafeRepository
 */
@Service
private class IdentifyServiceImpl @Autowired constructor(
    private val decodeJwtTokenUseCase: DecodeJwtTokenUseCase,
    private val userSafeRepository: UserSafeRepository
) : IdentifyService {

    override suspend fun getUserByRefreshToken(refreshToken: String): User {
        val jwtModel = decodeJwtTokenUseCase.verify(refreshToken, GlobalConstants.REFRESH_TOKEN_TYPE)
        return (userSafeRepository.findUserByRefreshId(jwtModel.jwtId)
            ?: throw UserNotFoundException("User by refreshToken $refreshToken not found")).toModel()
    }

}