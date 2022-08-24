package ru.kyamshanov.mission.authentication.controllers

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.kyamshanov.mission.authentication.GlobalConstants.KEY_ENABLED_TEST_CONTROLLER
import java.util.UUID

/**
 * Тестовый контролер
 * Работает только при [KEY_ENABLED_TEST_CONTROLLER] - true в application.properties
 */
@RestController()
@RequestMapping("/auth")
@ConditionalOnProperty(name = [KEY_ENABLED_TEST_CONTROLLER], havingValue = true.toString())
class TestController {

    /**
     * End-point: test
     * @return [ResponseEntity] с сгенерированным [UUID]
     */
    @GetMapping("test")
    suspend fun test(): ResponseEntity<String> =
        ResponseEntity<String>(
            UUID.randomUUID().toString(),
            HttpStatus.OK
        )
}