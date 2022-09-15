package ru.kyamshanov.mission.authentication.configuration

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import ru.kyamshanov.mission.authentication.GlobalConstants

/**
 * Квалификатор для определения - время жизни refresh токена в секундах
 */
@Qualifier
internal annotation class RefreshTokenTimeLifeInSec

/**
 * Квалификатор для определения - время жизни access токена в секундах
 */
@Qualifier
internal annotation class AccessTokenTimeLifeInSec

/**
 * Конфигурация обработчиков
 * @property refreshTokenTimeLife Время жизни refresh токена в сек
 * @property accessTokenTimeLife Время жизни access токена в сек
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
}