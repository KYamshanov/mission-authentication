package ru.kyamshanov.mission.authentication.propcessors

import ru.kyamshanov.mission.authentication.entities.toEntity
import ru.kyamshanov.mission.authentication.models.User
import ru.kyamshanov.mission.authentication.repositories.UserEntityCrudRepository

/**
 * Обработчик пользователей
 */
internal interface UserProcessor {

    /**
     * Сохранить пользователя
     * @param user Модель пользователя
     */
    fun saveUser(user: User)
}

/**
 * Реализация [UserProcessor]
 * @property userEntityCrudRepository CRUD репозиторий сущностей пользователей
 */
internal class UserProcessorImpl(
    private val userEntityCrudRepository: UserEntityCrudRepository
) : UserProcessor {

    /**
     * @see [UserProcessor.saveUser]
     */
    override fun saveUser(user: User) {
        userEntityCrudRepository.save(user.toEntity())
    }
}
