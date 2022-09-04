package ru.kyamshanov.mission.authentication.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.r2dbc.postgresql.codec.Json
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import ru.kyamshanov.mission.authentication.converters.DbJsonToMapConverter
import ru.kyamshanov.mission.authentication.converters.MapToJsonDbConverter
import ru.kyamshanov.mission.authentication.converters.TokenStatusConverter
import ru.kyamshanov.mission.authentication.entities.TokenStatus
import ru.kyamshanov.mission.authentication.models.JsonMap

/**
 * Конфигурация конвертеров
 */
@Configuration
internal class ConverterConfiguration {

    @Bean
    fun objectMapper(): ObjectMapper = jacksonObjectMapper().apply {
        registerModule(JavaTimeModule())
    }

    @Bean
    fun mapToJsonStringConverter(objectMapper: ObjectMapper): Converter<JsonMap, Json> =
        MapToJsonDbConverter(objectMapper)

    @Bean
    fun jsonStringToMapConverter(objectMapper: ObjectMapper): Converter<Json, JsonMap> =
        DbJsonToMapConverter(objectMapper)

    @Bean
    fun tokenStatusConverter(): Converter<TokenStatus, TokenStatus> =
        TokenStatusConverter()
}