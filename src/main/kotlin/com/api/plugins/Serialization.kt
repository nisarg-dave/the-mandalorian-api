package com.api.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.application.*


fun Application.configureSerialization() {
//  Installs ContentNegotiation plugin and enables the JSON serializer
    install(ContentNegotiation) {
        json()
    }
}