package ru.kyamshanov.mission.authentication.schedulers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import ru.kyamshanov.mission.authentication.repositories.SessionsSafeRepository
import java.util.concurrent.TimeUnit

/**
 * Планируемая задача по удалению токенов и истекшим сроком действия
 */
@Component
internal class ClearExpiredSessionsScheduledTask(
    private val sessionsSafeRepository: SessionsSafeRepository
) {

    private val componentCoroutineScope = CoroutineScope(Dispatchers.IO)

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS)
    fun clearSessionTokens() {
        componentCoroutineScope.launch {
            println("Deleting tokens")
            sessionsSafeRepository.deleteExpiredTokens()
        }
    }
}