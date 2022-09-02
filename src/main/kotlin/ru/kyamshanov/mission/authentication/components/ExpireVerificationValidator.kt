package ru.kyamshanov.mission.authentication.components

import org.springframework.stereotype.Component
import ru.kyamshanov.mission.authentication.errors.TokenExpireException
import java.time.Instant

/**
 * Средство проверки истечения срока действия
 * @property getCurrentInstantUseCase UseCase для получения текущй отметки времени
 */
@Component
internal class ExpireVerificationValidator(
    private val getCurrentInstantUseCase: GetCurrentInstantUseCase
) {

    /**
     * Валидировать  [Instant]
     * @param expiresAt Дата истечения срока действия
     */
    operator fun invoke(expiresAt: Instant) {
        if (getCurrentInstantUseCase() > expiresAt)
            throw TokenExpireException()
    }
}