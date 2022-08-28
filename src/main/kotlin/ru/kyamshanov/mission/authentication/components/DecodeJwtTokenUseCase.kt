package ru.kyamshanov.mission.authentication.components

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.stereotype.Component
import ru.kyamshanov.mission.authentication.GlobalConstants.CLAIM_TOKEN_TYPE
import ru.kyamshanov.mission.authentication.models.JwtModel

/**
 * UseCase для декодировки jwt токена
 * @property jwtVerifier Jwt верификатор
 */
@Component
internal class DecodeJwtTokenUseCase(
    private val jwtVerifier: JWTVerifier
) {

    /**
     * Декодировать jwt токен без проверки сигнатуры
     * @param token JWT токен
     * @return декодированную модель jwt [JwtModel]
     */
    fun decode(token: String): JwtModel =
        JWT.decode(token).toJwtModel()

    /**
     * Декторировать с проверкой сигнатуры
     * @param token jwt токен
     * @return декодированную модель jwt [JwtModel]
     */
    fun verify(token: String): JwtModel =
        jwtVerifier.verify(token).toJwtModel()

    private fun DecodedJWT.toJwtModel() = JwtModel(
        jwtId = requireNotNull(id),
        expiresAt = expiresAtAsInstant,
        subject = subject,
        type = requireNotNull(getClaim(CLAIM_TOKEN_TYPE).asString())
    )
}