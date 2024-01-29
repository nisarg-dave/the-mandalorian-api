package com.api.routes

import com.api.dao.quotes.quotesDAO
import com.api.models.*
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
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
            call.respondText("No quotes storage found.", status = HttpStatusCode.OK)
        }

    }
}

fun Route.getQuotesByCharacter(){
//    uses a lambda expression as router handler.
    // if I do this {character?} means character is optional but we don't want that
    get("/quote/character/{character}"){
//        val character  = call.parameters["character"] ?: call.respondText("Missing Character", status = HttpStatusCode.BadRequest)
//        val quotesByCharacter = quotesDAO.quotesByCharacter(call.parameters["character"]!!)
//        if(quotesByCharacter.isEmpty()){
//            call.respondText("Not Found", status = HttpStatusCode.NotFound)
//        }
//        call.respond(quotesByCharacter)
//        400 Bad request
        val character  = call.parameters["character"] ?: return@get call.respondText("Missing character.", status = HttpStatusCode.BadRequest)
        // 404 not found
        val quotes = quotesStorage.filter { it.character == character} ?: call.respondText("No character with name $character", status = HttpStatusCode.NotFound)
        call.respond(quotes)
    }
}

fun Route.getQuotesByShow(){
    get("/quote/show/{show}"){
        val show = call.parameters["show"] ?: return@get call.respondText("Missing show.", status = HttpStatusCode.BadRequest)
        val season = call.request.queryParameters["season"]
        if(season == null){
            val quotes = quotesStorage.filter { it.show == show } ?: return@get call.respondText("No show with name $show", status = HttpStatusCode.NotFound)
            call.respond(quotes)
        }
        else {
            val quotes = quotesStorage.filter { it.show == show && it.season == season.toInt() } ?: call.respondText("No show with name $show", status = HttpStatusCode.NotFound)
            call.respond(quotes)
        }
    }
}

fun Route.createQuote(){
    post("/quote"){
        // With generic parameter, it automatically deserializes the JSON request body into Quote object.
        val quote = call.receive<Quote>()
        quotesStorage.add(quote)
//       201 Created
        call.respondText("Quote stored correctly.", status = HttpStatusCode.Created)
    }

}

fun Route.deleteQuote(){
    delete("/quote/{id}") {
//        return statement means nothing below will process
        val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
        if(quotesStorage.removeIf { it.id == id.toInt()}){
// 202 Accepted
            call.respondText("Quote removed correctly.", status = HttpStatusCode.Accepted)
        }
        else {
//            404 not found
            call.respondText("Not Found", status = HttpStatusCode.NotFound)
        }
    }
}

fun Route.editQuote(){
    put("/quote/{id}"){
        val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
        val quoteToUpdate = quotesStorage.find {it.id == id.toInt()} ?: return@put call.respondText("Not found", status = HttpStatusCode.NotFound)
        val indexOfQuote = quotesStorage.indexOf(quoteToUpdate)
        println(indexOfQuote)
        quotesStorage[indexOfQuote] = call.receive<Quote>()
//        200 OK
        call.respondText("Updated correctly", status = HttpStatusCode.OK)
    }
}

