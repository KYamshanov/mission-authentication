package ru.kyamshanov.mission.authentication.propcessors

import org.springframework.beans.factory.annotation.Qualifier
import ru.kyamshanov.mission.authentication.UserInfoConstants.FINGERPRINT_KEY
import ru.kyamshanov.mission.authentication.models.JsonMap

/**
 * Квалификатор для [UserVerifyProcessorNormal]
 */
@Qualifier
internal annotation class UserVerifyNormal

/**
 * Реализация [UserVerifyProcessor] со средним уровнем сложности проверки
 * Для валидности информации необходимо поле `fingerprint`
 * Для соответствия данных пользователей используется отпечаток устройства (`fingerprint`)
 */
internal class UserVerifyProcessorNormal : UserVerifyProcessor {

    override fun checkInfo(info: JsonMap): Boolean =
        info.map[FINGERPRINT_KEY] != null

    override fun verify(savedInfo: JsonMap, currentInfo: JsonMap): Boolean {
        val savedFingerprint =
            requireNotNull(savedInfo.map[FINGERPRINT_KEY]) { "$FINGERPRINT_KEY was require for savedInfo" }
        val currentInfoFingerprint =
            requireNotNull(currentInfo.map[FINGERPRINT_KEY]) { "$FINGERPRINT_KEY was require for currentInfo" }
        return savedFingerprint == currentInfoFingerprint
    }
}