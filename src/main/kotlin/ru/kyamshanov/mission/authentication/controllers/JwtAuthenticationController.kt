package ru.kyamshanov.mission.authentication.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.kyamshanov.mission.authentication.factories.UserFactory
import ru.kyamshanov.mission.authentication.propcessors.UserProcessor

/**
 * Контроллер для JWT авторизации
 * @property userProcessor Обработчик пользователей
 * @property userFactory Фабрика пользователей
 */
@RestController()
@RequestMapping("/auth")
internal class JwtAuthenticationController @Autowired constructor(
    private val userProcessor: UserProcessor, private val userFactory: UserFactory
) {

    /**
     * End-point (POST) : /reg
     * Регистрация пользователя
     * @param body Тело запроса
     */
    @PostMapping("reg")
    fun registration(
        @RequestParam(required = true) body: RegistrationBody
    ): ResponseEntity<Unit> {
        val user = userFactory.createUser(body.login, body.password)
        userProcessor.saveUser(user)
        return ResponseEntity(HttpStatus.OK)
    }

    /**
     * Модель тела запроса для регистрации пользователя
     * @property login Имя пользователя
     * @property password Пароль
     */
    data class RegistrationBody(val login: String, val password: String)
}