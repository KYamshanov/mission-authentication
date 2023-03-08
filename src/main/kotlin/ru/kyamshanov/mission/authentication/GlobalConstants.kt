package ru.kyamshanov.mission.authentication

/**
 * Основные константы приложения
 */
object GlobalConstants {

    /**
     * Тип токена - access
     */
    const val ACCESS_TOKEN_TYPE = "access"

    /**
     * Тип токена - refresh
     */
    const val REFRESH_TOKEN_TYPE = "refresh"

    /**
     * Claim для хранения типа токена в JWT
     */
    const val CLAIM_TOKEN_TYPE = "ttype"

    /**
     * Claim для хранения ролей
     */
    const val CLAIM_ROLES = "roles"

    /**
     * Claim для хранения ролей
     */
    const val CLAIM_LOGIN = "login"

    /**
     * Источник сертификатов приложения
     */
    const val CERTIFICATES_PROPERTY_SOURCE = "classpath:certificates.properties"

    /**
     * Ключ для тогла - включен ли тестовый контроллер
     */
    const val KEY_ENABLED_TEST_CONTROLLER = "controller.test.enabled"

    /**
     * Ключ для параметра - время жизни рефреш токена
     */
    const val KEY_REFRESH_TIME_LIFE = "auth.jwt.$REFRESH_TOKEN_TYPE-life-time"

    /**
     * Ключ для параметра - время жизни токена доступности
     */
    const val KEY_ACCESS_TIME_LIFE = "auth.jwt.$ACCESS_TOKEN_TYPE-life-time"

    /**
     * Ключ для параметра - время жизни токена доступности
     */
    const val KEY_HOST = "postgres.host"

    /**
     * Ключ для параметра - время жизни токена доступности
     */
    const val KEY_PORT = "postgres.port"

    /**
     * Ключ для параметра - время жизни токена доступности
     */
    const val KEY_DATABASE = "postgres.database"

    /**
     * Ключ для параметра - время жизни токена доступности
     */
    const val KEY_SCHEMA = "postgres.schema"

    /**
     * Ключ для параметра - время жизни токена доступности
     */
    const val KEY_USERNAME = "certificate.database.username"

    /**
     * Ключ для параметра - время жизни токена доступности
     */
    const val KEY_PASSWORD = "certificate.database.password"

    /**
     * Ключ для параметра - приватный ключ для алгоритма HMAC256
     */
    const val KEY_ALGORITHM_SECRET = "algorithm.secret"

    /**
     * Ключ для параметра - время жизни auth-share токена
     */
    const val KEY_SHARE_TOKEN_LIFE_TIME = "auth.share-life-time"

    /**
     * Ключ для параметра - время жизни токена доступности
     */
    const val KEY_REDIS_HOST = "redis.host"

    /**
     * Ключ для параметра - время жизни токена доступности
     */
    const val KEY_REDIS_PORT = "redis.port"
}