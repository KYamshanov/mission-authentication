package ru.kyamshanov.mission.authentication.configuration

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import io.r2dbc.postgresql.codec.EnumCodec
import io.r2dbc.postgresql.codec.Json
import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement
import ru.kyamshanov.mission.authentication.GlobalConstants
import ru.kyamshanov.mission.authentication.entities.EntityStatus
import ru.kyamshanov.mission.authentication.models.JsonMap


/**
 * Конфигурация R2DBS, PostgresSQL
 */
@Configuration
@EnableR2dbcRepositories
@EnableTransactionManagement
internal class PostgresConfiguration @Autowired constructor(
    @Value("\${${GlobalConstants.KEY_HOST}}")
    private val host: String,
    @Value("\${${GlobalConstants.KEY_PORT}}")
    private val port: Int,
    @Value("\${${GlobalConstants.KEY_DATABASE}}")
    private val database: String,
    @Value("\${${GlobalConstants.KEY_SCHEMA}}")
    private val schema: String,
    @Value("\${${GlobalConstants.KEY_PASSWORD}}")
    private val password: CharSequence,
    @Value("\${${GlobalConstants.KEY_USERNAME}}")
    private val username: String,
    private val jsonMapStringToMapConverter: Converter<Json, JsonMap>,
    private val mapToJsonStringConverterMap: Converter<JsonMap, Json>,
    private val entityStatusConverter: Converter<EntityStatus, EntityStatus>,
    private val mapToEntityConverter: Converter<EntityStatus, EntityStatus>
) : AbstractR2dbcConfiguration() {

    @Bean
    override fun connectionFactory(): ConnectionFactory {
        return PostgresqlConnectionFactory(
            PostgresqlConnectionConfiguration.builder()
                .host(host)
                .port(port)
                .database(database)
                .username(username)
                .password(password)
                .schema(schema)
                .codecRegistrar(
                    EnumCodec.builder().withEnum("entity_status", EntityStatus::class.java).build()
                )
                .build()
        )
    }

    @Bean
    override fun r2dbcCustomConversions(): R2dbcCustomConversions =
        mutableListOf<Any>().apply {
            add(jsonMapStringToMapConverter)
            add(mapToJsonStringConverterMap)
            add(entityStatusConverter)
        }.let {
            R2dbcCustomConversions(storeConversions, it)
        }

    @Bean
    fun r2dbcEntityTemplate(): R2dbcEntityTemplate = R2dbcEntityTemplate(connectionFactory())

}