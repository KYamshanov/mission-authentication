package ru.kyamshanov.mission.authentication.configuration

import org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import ru.kyamshanov.mission.authentication.propcessors.UserProcessor
import ru.kyamshanov.mission.authentication.propcessors.UserProcessorImpl
import ru.kyamshanov.mission.authentication.repositories.UserEntityCrudRepository

/**
 * Конфигурация обработчиков
 */
@Configuration
internal class ProcessorsConfiguration {

    /**
     * Бин singleton - [UserProcessor]
     */
    @Bean
    @Scope(value = SCOPE_SINGLETON)
    fun userProcessor(userEntityCrudRepository: UserEntityCrudRepository): UserProcessor =
        UserProcessorImpl(userEntityCrudRepository)

}