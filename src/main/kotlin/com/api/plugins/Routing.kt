package com.api.plugins

import com.api.routes.*
import io.ktor.server.routing.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    // Take trailing lambda as a parameter
    routing {
//        The routing function establishes a context where an implicit Route receiver is available within its lambda.
        getRandomQuotes()
        getQuotesByCharacter()
        getQuotesByShow()
        createQuote()
        deleteQuote()
        editQuote()
        getRandomCharacter()
        getCharacterByName()
    }
}
