package com.api.dao.users

import at.favre.lib.crypto.bcrypt.BCrypt
import com.api.dao.DatabaseFactory.dbQuery
import com.api.models.User
import com.api.models.Users
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class UsersDaoFacadeImpl : UsersDaoFacade {
    private fun resultRowToUser(row: ResultRow): User = User(
        id = row[Users.id],
        username = row[Users.username],
        password = row[Users.password]
    )

    override suspend fun findUserByUsername(username: String): User? {
        return dbQuery {
            Users.select{Users.username eq username }.map(::resultRowToUser).singleOrNull()
        }
    }
}

fun insertUser(config: ApplicationConfig){
    val adminUserUsername = config.property("admin-credentials.username").getString()
    val adminUserPassword = config.property("admin-credentials.password").getString()
    val hashedPassword = BCrypt.withDefaults().hashToString(12, adminUserPassword.toCharArray())
    transaction {
        Users.insert {
            it[this.username] = adminUserUsername
            it[this.password] = hashedPassword
        }
    }
}

val userDao = UsersDaoFacadeImpl()