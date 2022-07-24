package ru.kyamshanov.mission.authentication.configuration

import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import ru.kyamshanov.mission.authentication.factories.UserFactory
import ru.kyamshanov.mission.authentication.factories.UserFactoryImpl

/**
 * Конфигурация фбрик
 */
@Configuration
internal class FactoriesConfiguration {

    /**
     * Бин singleton - [UserFactory]
     */
    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    fun userFactoryBean(): UserFactory = UserFactoryImpl()

}