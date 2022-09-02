package ru.kyamshanov.mission.authentication.components

import org.springframework.stereotype.Component
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
    fun createUser(login: String, password: CharSequence): User
}

/**
 * Реализация [UserFactory]
 */
@Component
private class UserFactoryImpl : UserFactory {

    /**
     * @see [UserFactory.createUser]
     */
    override fun createUser(login: String, password: CharSequence) =
        User(login, password)
}