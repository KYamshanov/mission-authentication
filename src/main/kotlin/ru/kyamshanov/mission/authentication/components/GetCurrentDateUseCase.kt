package ru.kyamshanov.mission.authentication.components

import org.springframework.stereotype.Component
import java.util.*

/**
 * UseCase для получения текущей даты
 */
@Component
internal class GetCurrentDateUseCase {

    /**
     * Получить текущую дату
     * @return Дату в формате [Date]
     */
    operator fun invoke(): Date = Date()
}