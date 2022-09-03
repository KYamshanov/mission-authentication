package ru.kyamshanov.mission.authentication

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.PropertySources
import ru.kyamshanov.mission.authentication.GlobalConstants.CERTIFICATES_PROPERTY_SOURCE
import java.time.ZoneId

/**
 * Конфигруация приложения
 */
@Configuration
@PropertySources(PropertySource(value = [CERTIFICATES_PROPERTY_SOURCE]))
class ApplicationConfiguration {

    @Bean
    fun applicationTimeZone(): ZoneId = ZoneId.of("Europe/Moscow")
}