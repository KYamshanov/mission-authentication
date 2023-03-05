package ru.kyamshanov.mission.authentication.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.kyamshanov.mission.authentication.models.User
import ru.kyamshanov.mission.authentication.propcessors.UserProcessor

/**
 * Сервис регистрации
 */
internal interface RegistrationService {

    /**
     * Зарегистрировать нового пользователя
     * @param user Модель юзера
     *
     */
    suspend fun registration(user: User): User
}

/**
 * Реализация [RegistrationService]
 * @property userProcessor Обработчик пользователя
 */
@Service
private class RegistrationServiceImpl @Autowired constructor(
    private val userProcessor: UserProcessor,
) : RegistrationService {

    override suspend fun registration(user: User): User =
        userProcessor.saveUser(user)
}