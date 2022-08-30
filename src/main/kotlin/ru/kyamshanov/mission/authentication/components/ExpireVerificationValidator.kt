package ru.kyamshanov.mission.authentication.components

import org.springframework.stereotype.Component
import ru.kyamshanov.mission.authentication.errors.TokenExpireException
import java.time.Instant
import java.time.LocalDateTime

/**
 * Средство проверки истечения срока действия
 * @property getCurrentLocalDateTimeUseCase UseCase для получения даты без тайм зоны
 * @property getCurrentInstantUseCase UseCase для получения текущй отметки времени
 */
@Component
internal class ExpireVerificationValidator(
    private val getCurrentLocalDateTimeUseCase: GetCurrentLocalDateTimeUseCase,
    private val getCurrentInstantUseCase: GetCurrentInstantUseCase
) {

    /**
     * Валидировать  [LocalDateTime]
     * @param expiresAt Дата истечения срока действия
     */
    operator fun invoke(expiresAt: LocalDateTime) {
        if (getCurrentLocalDateTimeUseCase() > expiresAt)
            throw TokenExpireException()
    }

    /**
     * Валидировать  [Instant]
     * @param expiresAt Дата истечения срока действия
     */
    operator fun invoke(expiresAt: Instant) {
        if (getCurrentInstantUseCase() > expiresAt)
            throw TokenExpireException()
    }
}