package com.api.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

@Serializable
data class Quote(val id:Int, val show: String, val season: Int, val episode: String, val character: String, val quote: String)

@Serializable
data class QuoteContent(val show: String, val season: Int, val episode: String, val character: String, val quote: String)

object Quotes: Table(){
    val id = integer("id").autoIncrement()
    val show = varchar("show", 25)
    val season = integer("season")
    val episode =  varchar("episode", 20)
    val characterId = reference("character_id", Characters.id, onDelete = ReferenceOption.RESTRICT, onUpdate = ReferenceOption.CASCADE)
    val quote = varchar("quote", 1024)

    override val primaryKey = PrimaryKey(id)
}