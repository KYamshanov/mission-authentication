package ru.kyamshanov.mission.authentication.dto

import ru.kyamshanov.mission.authentication.models.SessionInfo

internal fun SessionInfo.toSessionDto() = GetAllSessionsRsDto.Session(
    id = id,
    status = status.toStatusDto()
)

private fun SessionInfo.Status.toStatusDto(): GetAllSessionsRsDto.Session.Status = when (this) {
    SessionInfo.Status.ACTIVE -> GetAllSessionsRsDto.Session.Status.ACTIVE
    SessionInfo.Status.PAUSED -> GetAllSessionsRsDto.Session.Status.PAUSED
    SessionInfo.Status.BLOCKED -> GetAllSessionsRsDto.Session.Status.BLOCKED
    SessionInfo.Status.INVALID -> GetAllSessionsRsDto.Session.Status.INVALID
}