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
import io.ktor.server.plugins.swagger.*
import io.ktor.server.routing.*

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
//  These function itself is defined as an extension function for the Application class. This means it's specifically designed to operate within the context of an Application object, leveraging its properties and methods.
//  Calling it outside this context would be like trying to call a method on an object that doesn't exist.
    configureSerialization()
    configureAuthentication()
    configureCors()
    characterRoutes()
    quoteRoutes()
    planetRoutes()
    authRoute()
    routing {
//      This method tries to look up the OpenAPI specification in the application resources. Otherwise, it tries to read the OpenAPI specification from the file system using java.io.File.
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
    }
}