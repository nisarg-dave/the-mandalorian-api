package com.api

import com.api.dao.DatabaseFactory
import com.api.dao.users.insertUser
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.api.plugins.*
import com.api.routes.authRoute
import com.api.routes.characterRoutes
import com.api.routes.planetRoutes
import com.api.routes.quoteRoutes

//fun main() {
//    // We are using embeddedServer instead of application.conf file to create the server
//    // embeddedServer configures server parameter
////    embeddedServer(Netty, port = 8080, host = "127.0.0.1", module = Application::module)
////        .start(wait = true)
//}

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    DatabaseFactory.init(environment.config)
    insertUser(environment.config)
    configureSerialization()
    characterRoutes()
    quoteRoutes()
    planetRoutes()
    authRoute()
}
