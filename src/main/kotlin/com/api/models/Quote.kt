package com.api.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Quote(val id:Int, val quote: String, val season: Int, val chapter: String, val character: String)

object Quotes: Table(){
    val id = integer("id").autoIncrement()
    val quote = varchar("quote", 1024)
    val season = integer("season")
    val chapter =  varchar("chapter", 20)
    val character = varchar("character", 20)

    override val primaryKey = PrimaryKey(id)
}

//val quotesStorage = listOf<Quote>()
