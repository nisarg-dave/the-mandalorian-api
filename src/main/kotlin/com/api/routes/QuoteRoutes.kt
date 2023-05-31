package com.api.routes

import com.api.dao.quotes.quotesDAO
import com.api.models.*
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getRandomQuotes() {
    get("/quote"){
        call.respond(quotesDAO.randomQuote()!!)
    }
}

fun Route.getQuotesByCharacter(){
    get("/quote/character/{character}"){
        val character  = call.parameters["character"] ?: call.respondText("Missing Character", status = HttpStatusCode.BadRequest)
        val quotesByCharacter = quotesDAO.quotesByCharacter(call.parameters["character"]!!)
        if(quotesByCharacter.isEmpty()){
            call.respondText("Not Found", status = HttpStatusCode.NotFound)
        }
        call.respond(quotesByCharacter)
    }
}

fun Route.getQuotesFromSeason(){
    get("/quote/season/{season}"){
        val season = call.parameters["season"] ?: call.respondText("Missing Season", status = HttpStatusCode.BadRequest)
        val quotesFromSeason = quotesDAO.quotesFromSeason(call.parameters["season"]!!.toInt())
        if(quotesFromSeason.isEmpty()){
            call.respondText("Not Found", status = HttpStatusCode.NotFound)
        }
        call.respond(quotesFromSeason)
    }
}



