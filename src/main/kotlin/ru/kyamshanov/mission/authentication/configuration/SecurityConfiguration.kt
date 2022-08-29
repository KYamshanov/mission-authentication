package ru.kyamshanov.mission.authentication.configuration

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import ru.kyamshanov.mission.authentication.GlobalConstants.KEY_ALGORITHM_SECRET

/**
 * Конфигурация безопасности
 * @property secret Приватный ключ для подписки JWT
 */
@Configuration
internal class SecurityConfiguration(
    @Value("\${$KEY_ALGORITHM_SECRET}")
    private val secret: ByteArray
) {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    fun providePasswordEncoder(): PasswordEncoder =
        Argon2PasswordEncoder()

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    fun jwtAlgorithm(): Algorithm =
        Algorithm.HMAC256(secret)

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    fun jwtVerifier(algorithm: Algorithm): JWTVerifier =
        JWT.require(algorithm).build()
}