package ru.kyamshanov.mission.authentication.models

/**
 * Коробка для конвертации [Map]<key - [String], value - [Any]> в соответствующий JSON
 * @property map Мапа для конвертации в JSON
 */
internal class JsonMap(val map: Map<String, Any>) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as JsonMap
        if (map != other.map) return false
        return true
    }

    override fun hashCode(): Int {
        return map.hashCode()
    }
}