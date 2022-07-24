package ru.kyamshanov.mission.authentication.factories

import ru.kyamshanov.mission.authentication.models.User

/**
 * Фабрика пользователей
 */
internal interface UserFactory {

    /**
     * Создать новую модель пользователя
     * @param login Имя пользователя
     * @param password Пароль
     */
    fun createUser(login: String, password: String): User

}

/**
 * Реализация [UserFactory]
 */
internal class UserFactoryImpl : UserFactory {

    /**
     * @see [UserFactory.createUser]
     */
    override fun createUser(login: String, password: String) = User(login, password)
}