package ru.kyamshanov.mission.authentication.configuration

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.security.crypto.password.PasswordEncoder
import ru.kyamshanov.mission.authentication.GlobalConstants
import ru.kyamshanov.mission.authentication.propcessors.*
import ru.kyamshanov.mission.authentication.repositories.UserEntityCrudRepository

@Qualifier
internal annotation class RefreshTokenTimeLifeInSec

@Qualifier
internal annotation class AccessTokenTimeLifeInSec

/**
 * Конфигурация обработчиков
 */
@Configuration
internal class ProcessorsConfiguration(
    @Value("\${${GlobalConstants.KEY_REFRESH_TIME_LIFE}}")
    private val refreshTokenTimeLife: Long,
    @Value("\${${GlobalConstants.KEY_ACCESS_TIME_LIFE}}")
    private val accessTokenTimeLife: Long
) {

    @Bean
    @AccessTokenTimeLifeInSec
    @Scope(value = SCOPE_SINGLETON)
    fun bindAccessTokenTimeLife(): Long = accessTokenTimeLife

    @Bean
    @RefreshTokenTimeLifeInSec
    @Scope(value = SCOPE_SINGLETON)
    fun bindRefreshTokenTimeLife(): Long = refreshTokenTimeLife

    @Bean
    @Scope(value = SCOPE_SINGLETON)
    fun userProcessor(
        userEntityCrudRepository: UserEntityCrudRepository,
        passwordEncoder: PasswordEncoder
    ): UserProcessor =
        UserProcessorImpl(userEntityCrudRepository, passwordEncoder)

  /*  @Bean
    @Scope(value = SCOPE_SINGLETON)
    fun jwtProcessor(
        algorithm: Algorithm,
        sessionsSafeRepository: SessionsSafeRepository,
        getCurrentInstantUseCase: GetCurrentInstantUseCase,
        generateJwtTokenUseCase: GenerateJwtTokenUseCase,
        decodeJwtTokenUseCase: DecodeJwtTokenUseCase,
        @UserVerifyNormal
        userVerifyProcessor: UserVerifyProcessor,
        expireVerificationValidator: ExpireVerificationValidator
    ): TokenProcessor =
        TokenProcessorImpl(
            refreshTokenTimeLife = refreshTokenTimeLife,
            accessTokenTimeLife = accessTokenTimeLife,
            sessionsSafeRepository = sessionsSafeRepository,
            getCurrentInstantUseCase = getCurrentInstantUseCase,
            generateJwtTokenUseCase = generateJwtTokenUseCase,
            decodeJwtTokenUseCase = decodeJwtTokenUseCase,
            userVerifyProcessor = userVerifyProcessor,
            expireVerificationValidator = expireVerificationValidator
        )*/

    @Bean
    @Scope(value = SCOPE_SINGLETON)
    @UserVerifyNormal
    fun userVerifyProcessorNormal(): UserVerifyProcessor =
        UserVerifyProcessorNormal()
}