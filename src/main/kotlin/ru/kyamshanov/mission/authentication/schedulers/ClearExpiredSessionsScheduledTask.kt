package ru.kyamshanov.mission.authentication.schedulers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import ru.kyamshanov.mission.authentication.repositories.BlockedAccessTokenRepository
import ru.kyamshanov.mission.authentication.repositories.TokenSafeRepository
import java.util.concurrent.TimeUnit

/**
 * Планируемая задача по удалению токенов и истекшим сроком действия
 */
@Component
internal class ClearExpiredSessionsScheduledTask(
    private val safeRepository: TokenSafeRepository,
    private val accessTokenRepository: BlockedAccessTokenRepository
) {

    private val componentCoroutineScope = CoroutineScope(Dispatchers.IO)

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS)
    fun clearSessionTokens() {
        componentCoroutineScope.launch {
            println("Deleting tokens")
            safeRepository.deleteExpiredTokens()
        }
    }

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS)
    fun clearBlockedAccessTokens() {
        componentCoroutineScope.launch {
            println("Deleting access tokens")
            accessTokenRepository.clearExpiredTokens()
        }
    }
}