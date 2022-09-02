package ru.kyamshanov.mission.authentication.converters

import org.springframework.data.r2dbc.convert.EnumWriteSupport
import ru.kyamshanov.mission.authentication.entities.EntityStatus

/**
 * Конвертер [EntityStatus] для поддержки `entity_status` в БД
 */
internal class TokenStatusConverter : EnumWriteSupport<EntityStatus>()
