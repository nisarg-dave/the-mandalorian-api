package com.api.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Character(val id: Int, val name: String, val description: String)

@Serializable
data class CharacterPostBody(val name: String, val description: String)

// Each val becomes Column<Something type>
object Characters : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 20)
    val description = varchar("description", length = 1024)

    override val primaryKey = PrimaryKey(id)
}