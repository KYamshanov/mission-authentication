package ru.kyamshanov.mission.authentication.components

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.DecodedJWT
import org.springframework.stereotype.Component
import ru.kyamshanov.mission.authentication.GlobalConstants.CLAIM_ROLES
import ru.kyamshanov.mission.authentication.GlobalConstants.CLAIM_TOKEN_TYPE
import ru.kyamshanov.mission.authentication.errors.TokenTypeException
import ru.kyamshanov.mission.authentication.models.JwtModel
import ru.kyamshanov.mission.authentication.models.UserRole

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
     * Декодировать с проверкой сигнатуры
     *
     * @param token jwt токен
     * @param requireTokenType Запрашиваемый тип токена
     *
     * @return декодированную модель jwt [JwtModel]
     *
     * @throws TokenExpiredException Если истекло время жизни токена
     * @throws TokenTypeException Если тип токена не соответствует [requireTokenType]
     */
    fun verify(token: String, requireTokenType: String): JwtModel =
        jwtVerifier.verify(token).toJwtModel()
            .also { if (it.type != requireTokenType) throw TokenTypeException("required token type $requireTokenType but found ${it.type}") }

    private fun DecodedJWT.toJwtModel() = JwtModel(
        jwtId = requireNotNull(id),
        expiresAt = expiresAtAsInstant,
        externalUserId = subject,
        type = requireNotNull(getClaim(CLAIM_TOKEN_TYPE).asString()),
        roles = getClaim(CLAIM_ROLES).asList(String::class.java).map { UserRole.valueOf(it) },
    )
}