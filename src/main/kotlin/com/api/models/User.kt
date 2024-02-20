package com.api.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class User(val id: Int, val username: String, val password: String)

@Serializable
data class UserContent(val username: String, val password: String)

object Users: Table(){
    val id = integer("id").autoIncrement()
//    unique index ensures no two rows are the same
    val username = varchar("username", 128).uniqueIndex()
    val password = varchar("password", 128)
}