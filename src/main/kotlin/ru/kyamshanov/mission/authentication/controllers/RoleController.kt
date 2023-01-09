package ru.kyamshanov.mission.authentication.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.kyamshanov.mission.authentication.dto.SetUserRolesRqDto
import ru.kyamshanov.mission.authentication.propcessors.UserProcessor
import ru.kyamshanov.mission.authentication.services.RoleService

/**
 * Контроллер ролей
 * @property roleService Сервис для управления ролями
 * @property userProcessor Обработчик пользователя
 */
@RestController
@RequestMapping("/roles")
internal class RoleController @Autowired constructor(
    private val roleService: RoleService,
    private val userProcessor: UserProcessor
) {

    /**
     * End-point (POST) : /set_role
     * Изменить роли пользователя
     * @param body [SetUserRolesRqDto] Тело запроса
     * @return Рузльтат регистрации в виде [ResponseEntity] без тела
     */
    @PostMapping("set_role")
    suspend fun registration(
        @RequestBody(required = false) body: SetUserRolesRqDto
    ): ResponseEntity<Unit> =
        try {
            val user = userProcessor.getUserById(body.externalUserId)
            roleService.setUserRoles(user, body.roles)
            ResponseEntity(HttpStatus.OK)
        } catch (e: Throwable) {
            e.printStackTrace()
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }

}