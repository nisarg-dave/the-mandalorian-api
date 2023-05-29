package com.api.routes

import com.api.models.*
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.random.Random

fun Route.getRandomQuotes() {
    get("/quote"){
        val randomNumber = Random.nextInt(1,27)
        val randomQuote = quotesStorage.find { it.id == randomNumber }
        call.respond(randomQuote!!)
    }
}

fun Route.getQuotesByCharacter(){
    get("/quote/character/{character}"){
        val character  = call.parameters["character"] ?: call.respondText("Missing Character", status = HttpStatusCode.BadRequest)
        val quotesByCharacter = quotesStorage.filter { it.character == character }
        if(quotesByCharacter.isEmpty()){
            call.respondText("Not Found", status = HttpStatusCode.NotFound)
        }
        call.respond(quotesByCharacter)
    }
}

fun Route.getQuotesFromSeason(){
    get("/quote/season/{season}"){
        val season = call.parameters["season"] ?: call.respondText("Missing Season", status = HttpStatusCode.BadRequest)
        val quotesFromSeason = quotesStorage.filter { it.season.toString() == season }
        if(quotesFromSeason.isEmpty()){
            call.respondText("Not Found", status = HttpStatusCode.NotFound)
        }
        call.respond(quotesFromSeason)
    }
}



