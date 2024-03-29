package com.api.dao

import com.api.models.Characters
import com.api.models.Planets
import com.api.models.Quotes
import com.api.models.Users
import com.zaxxer.hikari.*
import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init(config: ApplicationConfig){
        val driverClassName = config.property("storage.driverClassName").getString()
        val jdbcURL = config.property("storage.jdbcUrl").getString()
        val maxPoolSize = config.property("storage.maxPoolSize").getString()
        val autoCommit = config.property("storage.autoCommit").getString()
        val username = config.property("storage.user").getString()
        val password = config.property("storage.password").getString()
        val databaseName = config.property("storage.database").getString()
//      Enabling batched inserts
        val reWriteBatchedInserts = true
        val database = Database.connect(hikari(
            url = "$jdbcURL/$databaseName?user=$username&password=$password&reWriteBatchedInserts=$reWriteBatchedInserts",
            driver = driverClassName,
            maxPoolSize = maxPoolSize.toInt(),
            autoCommit = autoCommit.toBoolean()
        ))
        transaction(database){
            SchemaUtils.create(Quotes)
            SchemaUtils.create(Characters)
            SchemaUtils.create(Planets)
            SchemaUtils.create(Users)
        }
    }

    private fun hikari(url: String, driver: String, maxPoolSize: Int, autoCommit: Boolean): HikariDataSource {
        return HikariDataSource(HikariConfig().apply {
            driverClassName = driver
            jdbcUrl = url
            maximumPoolSize = maxPoolSize
            isAutoCommit = autoCommit
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        })
    }

//  A utility function that is used to query the database
    suspend fun <T> dbQuery(block: suspend () -> T): T {
        return newSuspendedTransaction(Dispatchers.IO) { block() }
    }
}