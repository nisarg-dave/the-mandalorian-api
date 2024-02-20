package com.api.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.application.*


fun Application.configureSerialization() {
//    second parameter of install is a lambda that invokes a function
//    installs ContentNegotiation plugin and enables the JSON serializer
    install(ContentNegotiation) {
//        registers application/json as a content type
        json()
    }
}
