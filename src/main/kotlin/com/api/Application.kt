package com.api

import com.api.dao.DatabaseFactory
import com.api.dao.users.insertUser
import io.ktor.server.application.*
import com.api.plugins.*
import com.api.routes.authRoute
import com.api.routes.characterRoutes
import com.api.routes.planetRoutes
import com.api.routes.quoteRoutes
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    DatabaseFactory.init(environment.config)
    insertUser(environment.config)
    configureSerialization()
    configureAuthentication()
    configureCors()
    characterRoutes()
    quoteRoutes()
    planetRoutes()
    authRoute()
    routing {
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
    }
}