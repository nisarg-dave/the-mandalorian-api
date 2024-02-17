package com.api.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Planet(val id: Int, val name: String, val description: String, val imgUrl: String)

@Serializable
data class PlanetContent(val name: String, val description: String, val imgUrl: String)

object Planets : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 35)
    val description = varchar("description", length = 1024)
    val imgUrl = varchar("imgUrl", length = 150)

    override val primaryKey = PrimaryKey(id)
}