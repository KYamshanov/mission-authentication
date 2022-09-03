package ru.kyamshanov.mission.authentication.components

import org.springframework.stereotype.Component
import java.time.Instant

/**
 * UseCase для получения текущй отметки времени
 */
@Component
internal class GetCurrentInstantUseCase {

    /**
     * Получить текущую дату
     * @return Дату в формате [Instant]
     */
    operator fun invoke(): Instant = Instant.now()
}

