package com.api

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.api.plugins.*

fun main() {
    // We are using embeddedServer instead of application.conf file to create the server
    // embeddedServer configures server parameter
    embeddedServer(Netty, port = 8080, host = "127.0.0.1", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
}
