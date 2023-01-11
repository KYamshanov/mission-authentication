package ru.kyamshanov.mission.authentication.schedulers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import ru.kyamshanov.mission.authentication.repositories.SessionsSafeRepository
import java.util.concurrent.TimeUnit

/**
 * Планируемая задача по удалению токенов и истекшим сроком действия
 * @property sessionsSafeRepository Безопасный репозиторий для взаимодействия с сессиями
 */
@Component
internal class ClearExpiredSessionsScheduledTask(
    private val sessionsSafeRepository: SessionsSafeRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val componentCoroutineScope = CoroutineScope(Dispatchers.IO)

    /**
     * Задача по удалению истекших токенов
     */
    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS)
    fun clearSessionTokens() {
        componentCoroutineScope.launch {
            sessionsSafeRepository.deleteExpiredTokens()
                ?.also { if (it > 0) logger.debug("$it tokens in redis db deleted") }
        }
    }
}