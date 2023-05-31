package com.api.dao

import com.zaxxer.hikari.*
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database


object DatabaseFactory {
    fun init(config: ApplicationConfig){
        val driverClassName = config.property("storage.driverClassName").getString()
        val jdbcURL = config.property("storage.jdbcUrl").getString()
        val maxPoolSize = config.property("storage.maxPoolSize").getString()
        val autoCommit = config.property("storage.autoCommit").getString()
        val username = config.property("storage.user").getString()
        val password = config.property("storage.password").getString()
        val databaseName = config.property("storage.database").getString()
        val database = Database.connect(hikari(
            url = "$jdbcURL/$databaseName?user=$username&password=$password",
            driver = driverClassName,
            maxPoolSize = maxPoolSize.toInt(),
            autoCommit = autoCommit.toBoolean()
        ))
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
}