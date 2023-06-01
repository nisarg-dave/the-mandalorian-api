package com.api.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Character(val id: Int, val name: String)

object Characters : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 20)

    override val primaryKey = PrimaryKey(id)

}
//val charactersStorage = listOf<Character>()