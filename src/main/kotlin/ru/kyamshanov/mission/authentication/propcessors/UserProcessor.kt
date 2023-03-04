package ru.kyamshanov.mission.authentication.propcessors

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import ru.kyamshanov.mission.authentication.entities.UserEntity
import ru.kyamshanov.mission.authentication.errors.NoMatchPasswordException
import ru.kyamshanov.mission.authentication.errors.UserAlreadySavedException
import ru.kyamshanov.mission.authentication.errors.UserNotFoundException
import ru.kyamshanov.mission.authentication.models.User
import ru.kyamshanov.mission.authentication.models.toModel
import ru.kyamshanov.mission.authentication.repositories.UserEntityCrudRepository

/**
 * Обработчик пользователей
 */
internal interface UserProcessor {

    /**
     * Сохранить пользователя
     * @param user Модель пользователя
     * @return [User] пользователя с зашифрованным паролем и установленным id
     *
     * @throws UserAlreadySavedException Если пользователь уже сохранен
     */
    suspend fun saveUser(user: User): User

    /**
     * Провериь пользователя
     * @param user Модель пользователя
     * @return Модель [User] из БД
     *
     * @throws UserNotFoundException если пользователь не найден в БД
     * @throws IllegalArgumentException Если [User.id] - null
     * @throws NoMatchPasswordException Если пароль не соответствует с сохраненным
     */
    suspend fun verify(user: User): User

    /**
     * Получить пользователя из Id
     * @param userId Идентификатор пользователя
     * @return [User]
     */
    suspend fun getUserById(userId: String): User
}

/**
 * Реализация [UserProcessor]
 * @property userEntityCrudRepository CRUD репозиторий сущностей пользователей
 * @property passwordEncoder Кодировщик паролей
 */
@Component
private class UserProcessorImpl(
    private val userEntityCrudRepository: UserEntityCrudRepository,
    private val passwordEncoder: PasswordEncoder
) : UserProcessor {

    /**
     * @see [UserProcessor.saveUser]
     */
    override suspend fun saveUser(user: User): User {
        val entity = UserEntity(
            login = user.login,
            password = passwordEncoder.encode(user.password),
        )
        return userEntityCrudRepository.save(entity).toModel()
    }

    /**
     * @see [UserProcessor.verify]
     */
    override suspend fun verify(user: User): User {
        val foundUser = userEntityCrudRepository.findByLogin(user.login)
            ?: throw UserNotFoundException("User with login ${user.login} has not found in auth-users database")
        if (!passwordEncoder.matches(user.password, foundUser.password.toString()))
            throw NoMatchPasswordException()
        return foundUser.toModel()
    }

    override suspend fun getUserById(userId: String): User =
        userEntityCrudRepository.findById(userId)?.toModel()
            ?: throw UserNotFoundException("User with Id $userId has not found in auth-users database")
}
