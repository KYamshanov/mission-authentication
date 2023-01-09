package ru.kyamshanov.mission.authentication.services

import org.springframework.stereotype.Service
import java.util.*

/**
 * Сервис генерации идентификаторов
 */
internal interface IdGenerationService {

    /**
     * Сгенерировать новый идентификатор
     * @return [String]
     */
    suspend fun generate(): String
}

/**
 * Реализация [IdGenerationService]
 */
@Service
private class IdGenerationServiceImpl : IdGenerationService {

    override suspend fun generate(): String =
        UUID.randomUUID().toString()
}