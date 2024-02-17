package com.api.dao

import com.api.models.Characters
import com.api.models.Planets
import com.api.models.Quotes
import com.zaxxer.hikari.*
import io.ktor.server.config.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction


object DatabaseFactory {
    fun init(config: ApplicationConfig){
//        A data access object is pattern that provides an interface to a database without exposing the details of the database.
//        Every database connection using Exposed requires the JDBC url and driver class name
        val driverClassName = config.property("storage.driverClassName").getString()
        val jdbcURL = config.property("storage.jdbcUrl").getString()
        val maxPoolSize = config.property("storage.maxPoolSize").getString()
        val autoCommit = config.property("storage.autoCommit").getString()
        val username = config.property("storage.user").getString()
        val password = config.property("storage.password").getString()
        val databaseName = config.property("storage.database").getString()
        val reWriteBatchedInserts = true
        val database = Database.connect(hikari(
            url = "$jdbcURL/$databaseName?user=$username&password=$password&reWriteBatchedInserts=$reWriteBatchedInserts",
            driver = driverClassName,
            maxPoolSize = maxPoolSize.toInt(),
            autoCommit = autoCommit.toBoolean()
        ))
//       CRUD operations in Exposed must be called from within a transaction. Transactions encapsulate a set of DSL operations.
        transaction(database){
//            After obtaining the connection, all SQL statements should be placed inside a transaction
//            SchemaUtils has utility functions that assist with creating, altering, and dropping database schema objects.
            SchemaUtils.create(Quotes)
            SchemaUtils.create(Characters)
            SchemaUtils.create(Planets)
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

//    A utility function that is used to query the database and makes use of coroutines
//    Creates a new TransactionScope then calls the specified suspending statement, suspends until it completes, and returns the result.
//    Because Transactions are executed synchronously on the current thread, so they will block other parts of your application!
//    And because Exposed interacts with databases using JDBC API, which was designed in an era of blocking APIs
//    bridge functions are available that give you a safe way to interact with Exposed within suspend blocks e.g., new SuspendedTransaction
//    These have the same parameters as a blocking transaction() but allow you to provide a CoroutineContext argument that explicitly specifies the CoroutineDispatcher in which the function will be executed.
//    This is still blocking but newSuspendedTransaction, calls another suspend function which is the exposes orm code and waits for that to complete but runs it all in the IO thread, so that the main thread isn't blocked.
    suspend fun <T> dbQuery(block: suspend () -> T): T {
//        Running on IO thread
        return newSuspendedTransaction(Dispatchers.IO) { block() }
    }
}