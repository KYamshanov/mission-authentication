package ru.kyamshanov.mission.authentication.components

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.stereotype.Component
import ru.kyamshanov.mission.authentication.GlobalConstants.CLAIM_TOKEN_TYPE
import ru.kyamshanov.mission.authentication.models.JwtModel

/**
 * UseCase для генерации JWT токена
 * @property algorithm Алгоритм для подписания jwt токена
 */
@Component
internal class GenerateJwtTokenUseCase(
    private val algorithm: Algorithm
) {

    /**
     * Генерация jwt токена
     * @param jwtModel JWT Модель [JwtModel]
     * @return Созданный JWT токен
     */
    operator fun invoke(jwtModel: JwtModel): String =
        JWT.create()
            .withJWTId(jwtModel.jwtId)
            .withSubject(jwtModel.subject)
            .withExpiresAt(jwtModel.expiresAt)
            .withClaim(CLAIM_TOKEN_TYPE, jwtModel.type)
            .sign(algorithm)
}