package com.api.routes

import com.api.dao.quotes.quotesDAO
import com.api.models.*
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.random.Random

fun Route.getRandomQuotes() {
//    Route.get and other http methods are extension functions
    get("/random/quote"){
//        call.respond(quotesDAO.randomQuote()!!)
        if(quotesStorage.isNotEmpty()){
            val randomNumber = Random.nextInt(1,81)
//            call.respond and takes a kotlin object and returns it serialized in a special format. We need the ContentNegotiation plugin which is already installed with JSON serializer.
//            When client makes a request, content negotiation examines the Accept headers and sees if it can serve the specific content type and if so, returns the result.
            call.respond(quotesStorage[randomNumber])
        }
        else {
            call.respondText("No quotes storage found", status = HttpStatusCode.OK)
        }

    }
}


fun Route.getQuotesByCharacter(){
//    uses a lambda expression as router handler.
    get("/quote/{character}"){
//        val character  = call.parameters["character"] ?: call.respondText("Missing Character", status = HttpStatusCode.BadRequest)
//        val quotesByCharacter = quotesDAO.quotesByCharacter(call.parameters["character"]!!)
//        if(quotesByCharacter.isEmpty()){
//            call.respondText("Not Found", status = HttpStatusCode.NotFound)
//        }
//        call.respond(quotesByCharacter)
//        400 Bad request
        val character  = call.parameters["character"] ?: call.respondText("Missing Character", status = HttpStatusCode.BadRequest)
        // 404 not found
        val quotes = quotesStorage.filter { it.character == character} ?: call.respondText("No character with name $character", status = HttpStatusCode.NotFound)
        call.respond(quotes)
    }
}

fun Route.getQuotesByShow(){
    get("/quote/{show}"){
        val show = call.parameters["show"] ?: call.respondText("Missing Show", status = HttpStatusCode.BadRequest)
        val quotes = quotesStorage.filter { it.show == show } ?: call.respondText("No show with name $show", status = HttpStatusCode.NotFound)
        call.respond(quotes)
    }
}
fun Route.getQuotesByShowAndSeason(){
//    get("/quote/show")
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



