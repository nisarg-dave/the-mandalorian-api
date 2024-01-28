package com.api.plugins

import com.api.routes.*
import io.ktor.server.routing.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    // Take trailing lambda as a parameter
    routing {
        getRandomQuotes()
        getQuotesByCharacter()
        getQuotesByShow()
//        getCharacter()
    }
}
