package ru.kyamshanov.mission.authentication.components

import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * UseCase для получения даты без тайм зоны
 */
@Component
internal class GetCurrentLocalDateTimeUseCase {

    /**
     * Получить текущую дату
     * @return Дату в формате [LocalDateTime]
     */
    operator fun invoke(): LocalDateTime = LocalDateTime.now()
}