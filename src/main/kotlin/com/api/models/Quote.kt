package com.api.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

// @Serializable means that Ktor knows how to serialize Quotes
@Serializable
data class Quote(val id:Int, val show: String, val season: Int, val episode: String, val character: String, val quote: String)

// creates an anonymous object that inherits the table class
object Quotes: Table(){
    val id = integer("id").autoIncrement()
    val show = varchar("show", 25)
    val season = integer("season")
    val episode =  varchar("episode", 20)
//    Preventing characters from being deleted if there is reference to it in quotes table and if character ID is updated in Characters table then character_id here is also updated but we use path parameter in PUT request and so ID will never update as iDs should be considered immutable
    val characterId = reference("character_id", Characters.id, onDelete = ReferenceOption.RESTRICT, onUpdate = ReferenceOption.CASCADE)
    val quote = varchar("quote", 1024)

    override val primaryKey = PrimaryKey(id)
}
















