package ru.kyamshanov.mission.authentication

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.PropertySources
import ru.kyamshanov.mission.authentication.Constants.CERTIFICATES_PROPERTY_SOURCE

/**
 * Конфигруация приложения
 */
@Configuration
@PropertySources(PropertySource(value = [CERTIFICATES_PROPERTY_SOURCE]))
class ApplicationConfiguration