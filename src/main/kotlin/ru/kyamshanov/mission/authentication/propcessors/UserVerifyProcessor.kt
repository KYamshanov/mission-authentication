package ru.kyamshanov.mission.authentication.propcessors

import ru.kyamshanov.mission.authentication.models.JsonMap

/**
 * Средство для проверки данных пользователя
 */
internal interface UserVerifyProcessor {

    /**
     * Проверить данные о пользователе
     * @param info Данные пользоваетеля
     * @return true - если информация валидна
     */
    fun checkInfo(info: JsonMap): Boolean

    /**
     * Сверить сохраненные и полученные данные пользователя
     * @param savedInfo Сохраненные данные
     * @param currentInfo Текущие данные
     * @return true - если данные соответствующие (не обязательно равные)
     */
    fun verify(savedInfo: JsonMap, currentInfo: JsonMap): Boolean
}
