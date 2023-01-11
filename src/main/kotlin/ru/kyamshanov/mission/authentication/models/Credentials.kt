package ru.kyamshanov.mission.authentication.models


/**
 * Права пользователя
 * @property roles Роли
 */
internal data class Credentials(
    var roles: Collection<UserRole>
) {

    companion object {
        /** Сущность с дефолтными правами*/
        val Empty = Credentials(listOf())
    }
}
