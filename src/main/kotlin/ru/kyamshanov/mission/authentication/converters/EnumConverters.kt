package ru.kyamshanov.mission.authentication.converters

import org.springframework.data.r2dbc.convert.EnumWriteSupport
import ru.kyamshanov.mission.authentication.entities.TokenStatus

/**
 * Конвертер [TokenStatus] для поддержки `token_status` в БД
 */
internal class TokenStatusConverter : EnumWriteSupport<TokenStatus>()
