package ru.kyamshanov.mission.authentication

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class AuthenticationApplication

fun main(args: Array<String>) {
    runApplication<AuthenticationApplication>(*args)
}
