package com.api.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Character(val id: Int, val name: String, val description: String, val imgUrl: String)

@Serializable
data class CharacterContent(val name: String, val description: String, val imgUrl: String)


object Characters : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 25)
    val description = varchar("description", length = 1024)
    val imgUrl = varchar("imgUrl", length = 150)

    override val primaryKey = PrimaryKey(id)
}