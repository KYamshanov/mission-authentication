package ru.kyamshanov.mission.authentication.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.kyamshanov.mission.authentication.components.UserFactory
import ru.kyamshanov.mission.authentication.dto.*
import ru.kyamshanov.mission.authentication.errors.UserInfoRequiredException
import ru.kyamshanov.mission.authentication.models.JsonMap
import ru.kyamshanov.mission.authentication.services.AuthenticationService
import ru.kyamshanov.mission.authentication.services.BlockingService
import ru.kyamshanov.mission.authentication.services.RegistrationService

/**
 * Контроллер для JWT авторизации
 * @property authenticationService Сервис аутентификации
 * @property registrationService Сервис регистрации
 * @property blockingService Сервис блокировки
 * @property userFactory Фабрика пользователя
 */
@RestController
@RequestMapping("/auth")
internal class JwtAuthenticationController @Autowired constructor(
    private val authenticationService: AuthenticationService,
    private val registrationService: RegistrationService,
    private val blockingService: BlockingService,
    private val userFactory: UserFactory
) {

    /**
     * End-point (POST) : /reg
     * Регистрация пользователя
     * @param body Тело запроса
     * @return Рузльтат регистрации в виде [ResponseEntity] без тела
     */
    @PostMapping("reg")
    suspend fun registration(
        @RequestBody(required = false) body: UserDto
    ): ResponseEntity<Unit> =
        try {
            val user = userFactory.createUser(body.login, body.password)
            registrationService.registration(user)
            ResponseEntity(HttpStatus.OK)
        } catch (e: Throwable) {
            e.printStackTrace()
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }


    /**
     * End-point (POST) : /login
     * Аутентификация пользователя
     * @param body Тело запроса
     * @return [ResponseEntity] Статус обработки с результатом
     */
    @PostMapping("login")
    suspend fun login(
        @RequestBody(required = true) body: UserDto
    ): ResponseEntity<TokensRsDto> =
        try {
            val user = userFactory.createUser(body.login, body.password)
            val userInfo = body.info ?: throw UserInfoRequiredException()
            authenticationService.login(user, JsonMap(userInfo)).let {
                TokensRsDto(it.accessToken, it.refreshToken)
            }.let { ResponseEntity(it, HttpStatus.OK) }
        } catch (e: Throwable) {
            e.printStackTrace()
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }

    /**
     * End-point (POST) : /check
     * Проверка access-token
     * @param body Тело запроса
     * @return [ResponseEntity] Статус активности токена
     */
    @PostMapping("check")
    suspend fun checkAccessToken(
        @RequestBody(required = true) body: CheckAccessRqDto
    ): ResponseEntity<Unit> =
        try {
            authenticationService.verifyAccess(body.accessToken)
            ResponseEntity(HttpStatus.OK)
        } catch (e: Throwable) {
            e.printStackTrace()
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }

    /**
     * End-point (POST) : /refresh
     * Обновить токены с проверкой актуальности рефреш токена
     * @param body Тело запроса
     * @return [ResponseEntity] С парой новых токенов
     */
    @PostMapping("refresh")
    suspend fun validateTokens(
        @RequestBody(required = true) body: RefreshRqDto
    ): ResponseEntity<TokensRsDto> =
        try {
            authenticationService.verifyAndUpdateRefreshToken(body.refreshToken, JsonMap(body.info)).let {
                TokensRsDto(it.accessToken, it.refreshToken)
            }.let { ResponseEntity(it, HttpStatus.OK) }
        } catch (e: Throwable) {
            e.printStackTrace()
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }

    /**
     * End-point (POST) : /block
     * Блокировка рефреш токена
     * @param body Тело запроса
     * @return [ResponseEntity] Статус обработки
     */
    @PostMapping("block")
    suspend fun blockToken(
        @RequestBody(required = true) body: BlockRqDto
    ): ResponseEntity<Unit> =
        try {
            blockingService.blockSession(body.refreshToken)
            ResponseEntity(HttpStatus.OK)
        } catch (e: Throwable) {
            e.printStackTrace()
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
}