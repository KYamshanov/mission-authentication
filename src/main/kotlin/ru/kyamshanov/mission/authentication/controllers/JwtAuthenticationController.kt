package ru.kyamshanov.mission.authentication.controllers

import com.auth0.jwt.exceptions.TokenExpiredException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.kyamshanov.mission.authentication.components.GenerateJwtTokenUseCase
import ru.kyamshanov.mission.authentication.dto.*
import ru.kyamshanov.mission.authentication.errors.SessionBlockedException
import ru.kyamshanov.mission.authentication.errors.TokenExpireException
import ru.kyamshanov.mission.authentication.errors.UserInfoRequiredException
import ru.kyamshanov.mission.authentication.models.JsonMap
import ru.kyamshanov.mission.authentication.models.JwtPair
import ru.kyamshanov.mission.authentication.models.User
import ru.kyamshanov.mission.authentication.services.*

/**
 * Контроллер для JWT авторизации
 * @property authenticationService Сервис аутентификации
 * @property registrationService Сервис регистрации
 * @property blockingService Сервис блокировки
 * @property shareAuthenticationService Сервис для внешней аутентификации по токену
 * @property verifyService Сервис проверки
 * @property sessionService Сессионный сервис
 * @property identifyService Сервис идентификации пользователя
 * @property generateJwtTokenUseCase UseCase для получения токена из Jwt
 */
@RestController
@RequestMapping("/auth")
internal class JwtAuthenticationController @Autowired constructor(
    private val authenticationService: AuthenticationService,
    private val registrationService: RegistrationService,
    private val blockingService: BlockingService,
    private val shareAuthenticationService: ShareAuthenticationService,
    private val verifyService: VerifyService,
    private val sessionService: SessionService,
    private val identifyService: IdentifyService,
    private val generateJwtTokenUseCase: GenerateJwtTokenUseCase
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
            val user = User(body.login, body.password)
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
            val user = User(body.login, body.password)
            val userInfo = body.info ?: throw UserInfoRequiredException()
            authenticationService.login(user, JsonMap(userInfo)).toTokenRs()
                .let { ResponseEntity(it, HttpStatus.OK) }
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
    ): ResponseEntity<CheckAccessRsDto> =
        try {
            val jwtModel = verifyService.verifyAccessToken(body.accessToken, body.checkBlock)
            ResponseEntity(
                CheckAccessRsDto(
                    CheckAccessRsDto.AccessStatus.ACTIVE,
                    AccessDataDto(jwtModel.roles, jwtModel.externalUserId)
                ), HttpStatus.OK
            )
        } catch (e: TokenExpiredException) {
            ResponseEntity(CheckAccessRsDto(CheckAccessRsDto.AccessStatus.EXPIRED, null), HttpStatus.OK)
        } catch (e: TokenExpireException) {
            ResponseEntity(CheckAccessRsDto(CheckAccessRsDto.AccessStatus.EXPIRED, null), HttpStatus.OK)
        } catch (e: SessionBlockedException) {
            ResponseEntity(CheckAccessRsDto(CheckAccessRsDto.AccessStatus.BLOCKED, null), HttpStatus.OK)
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
    suspend fun refresh(
        @RequestBody(required = true) body: RefreshRqDto
    ): ResponseEntity<TokensRsDto> =
        try {
            authenticationService.refreshSession(body.refreshToken, JsonMap(body.info)).toTokenRs()
                .let { ResponseEntity(it, HttpStatus.OK) }
        } catch (e: Throwable) {
            e.printStackTrace()
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }

    /**
     * End-point (POST) : /block
     * Блокировка сессии
     * @param body Тело запроса
     * @return [ResponseEntity] Статус обработки
     */
    @PostMapping("block")
    suspend fun blockSession(
        @RequestBody(required = true) body: BlockRqDto
    ): ResponseEntity<Unit> =
        try {
            blockingService.blockSession(body.sessionId)
            ResponseEntity(HttpStatus.OK)
        } catch (e: Throwable) {
            e.printStackTrace()
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }

    /**
     * End-point (POST) : /block_refresh
     * Блокировка сессии по рефреш токену
     * @param body Тело запроса
     * @return [ResponseEntity] Статус обработки
     */
    @PostMapping("block_refresh")
    suspend fun blockRefresh(
        @RequestBody(required = true) body: BlockRefreshRqDto
    ): ResponseEntity<Unit> =
        try {
            blockingService.revokeRefreshToken(body.refreshToken)
            ResponseEntity(HttpStatus.OK)
        } catch (e: Throwable) {
            e.printStackTrace()
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }

    /**
     * End-point (POST) : /share
     * Создание токена для внешней авторизации
     * @param body Тело запроса
     * @return [ResponseEntity] Статус обработки
     */
    @PostMapping("share")
    suspend fun createShareAuthenticationToken(
        @RequestBody(required = true) body: ShareRqDto
    ): ResponseEntity<ShareRsDto> =
        try {
            val shareAuthToken = verifyService.verifyAccessToken(body.accessToken, true)
                .let { shareAuthenticationService.createShareAuthToken(it.jwtId) }
            ResponseEntity(ShareRsDto(shareAuthToken), HttpStatus.OK)
        } catch (e: Throwable) {
            e.printStackTrace()
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }

    /**
     * End-point (POST) : /share_login
     * Внешняя авторизация по токену
     * @param body Тело запроса
     * @return [ResponseEntity] Статус обработки
     */
    @PostMapping("share_login")
    suspend fun shareAuthentication(
        @RequestBody(required = true) body: LoginShareRqDto
    ): ResponseEntity<TokensRsDto> =
        try {
            val jwtPair = shareAuthenticationService.login(body.authShareToken, JsonMap(body.info))
            ResponseEntity(jwtPair.toTokenRs(), HttpStatus.OK)
        } catch (e: Throwable) {
            e.printStackTrace()
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }

    /**
     * End-point (POST) : /sessions
     * Получение всех сессий пользователя
     * @param body Тело запроса
     * @return [ResponseEntity] Статус обработки
     */
    @PostMapping("sessions")
    suspend fun shareAuthentication(
        @RequestBody(required = true) body: GetAllSessionsRqDto
    ): ResponseEntity<GetAllSessionsRsDto> =
        try {
            val sessions = sessionService.getAllSessionsByAccessToken(body.accessToken)
            ResponseEntity(GetAllSessionsRsDto(sessions), HttpStatus.OK)
        } catch (e: Throwable) {
            e.printStackTrace()
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }

    private fun JwtPair.toTokenRs(): TokensRsDto {
        val accessToken = generateJwtTokenUseCase(accessJwt)
        val refreshJwt = generateJwtTokenUseCase(refreshJwt)
        val roles = requireNotNull(accessJwt.roles) { "User roles needed for access token" }

        return TokensRsDto(AccessTokenDto(accessToken, AccessDataDto(roles, accessJwt.externalUserId)), refreshJwt)
    }
}