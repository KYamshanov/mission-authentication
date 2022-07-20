package ru.kyamshanov.mission.authentication

/**
 * Основные константы приложения
 */
object GlobalConstants {

    /**
     * Источник сертификатов приложения
     */
    const val CERTIFICATES_PROPERTY_SOURCE = "classpath:certificates.properties"

    /**
     * Ключ для тогла - включен ли тестовый контроллер
     */
    const val KEY_ENABLED_TEST_CONTROLLER = "controller.test.enabled"
}