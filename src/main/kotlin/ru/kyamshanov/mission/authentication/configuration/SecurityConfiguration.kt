package ru.kyamshanov.mission.authentication.configuration

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Конфигурация безопасности
 */
@Configuration
internal class SecurityConfiguration {

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    fun providePasswordEncoder(): PasswordEncoder =
        BCryptPasswordEncoder()

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    fun jwtAlgorithm(): Algorithm =
        Algorithm.HMAC256("secret")

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    fun jwtVerifier(algorithm: Algorithm): JWTVerifier =
        JWT.require(algorithm).build()
}