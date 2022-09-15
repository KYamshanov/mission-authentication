package ru.kyamshanov.mission.authentication.entities

import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import java.util.*

/**
 * Абстрактная сущность
 * @property givenId Id сущности в БД, null - сущность не сохранена
 */
internal abstract class AbstractEntity(
    @Transient
    private val givenId: String?
) : Persistable<String> {

    @delegate:Transient
    private val generatedId by lazy { UUID.randomUUID().toString().replace("-", "") }

    /**
     * @see [Persistable.getId]
     * Возвращает [givenId] либо сгенерированный Id для сущности [generatedId]
     */
    override fun getId(): String = givenId ?: generatedId

    /**
     * @see [Persistable.isNew]
     */
    override fun isNew(): Boolean = givenId == null

    /**
     * @see [Nothing.hashCode]
     */
    override fun hashCode(): Int = givenId.hashCode()

    /**
     * @see [Nothing.equals]
     */
    override fun equals(other: Any?): Boolean {
        return when {
            this === other -> true
            other == null -> false
            other !is AbstractEntity -> false
            else -> id == other.id
        }
    }
}