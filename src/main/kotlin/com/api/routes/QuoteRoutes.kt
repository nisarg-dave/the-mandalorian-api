package com.api.routes

import com.api.dao.quotes.quotesDAO
import com.api.models.*
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.quoteRoutes(){
    routing {
        getRandomQuotes()
        getQuotesByCharacter()
        getQuotesByShow()
        createQuote()
        deleteQuote()
        editQuote()
    }
}

fun Route.getRandomQuotes() {
//    Route.get and other http methods are extension functions
    get("/random/quote"){
        call.respond(quotesDAO.randomQuote() ?: call.respondText("No quotes storage found.", status = HttpStatusCode.OK))
//        if(quotesStorage.isNotEmpty()){
//            val randomNumber = Random.nextInt(1,81)
////            call.respond and takes a kotlin object and returns it serialized in a special format. We need the ContentNegotiation plugin which is already installed with JSON serializer.
////            When client makes a request, content negotiation examines the Accept headers and sees if it can serve the specific content type and if so, returns the result.
//            call.respond(quotesStorage[randomNumber])
//        }
//        else {
//            call.respondText("No quotes storage found.", status = HttpStatusCode.OK)
//        }
    }
}

fun Route.getQuotesByCharacter(){
//    uses a lambda expression as router handler.
    // if I do this {character?} means character is optional, but we don't want that
    get("/quotes/character/{character}"){
        val character  = call.parameters["character"] ?: return@get call.respondText("Missing character.", status = HttpStatusCode.BadRequest)
        val quotesByCharacter = quotesDAO.quotesByCharacter(character)
        if(quotesByCharacter.isEmpty()){
            call.respondText("Not Found", status = HttpStatusCode.NotFound)
        }
        call.respond(quotesByCharacter)
//        400 Bad request
//        val character  = call.parameters["character"] ?: return@get call.respondText("Missing character.", status = HttpStatusCode.BadRequest)
//        // 404 not found
//        val quotes = quotesStorage.filter { it.character == character} ?: call.respondText("No character with name $character", status = HttpStatusCode.NotFound)
//        call.respond(quotes)
    }
}

fun Route.getQuotesByShow(){
    get("/quotes/show/{show}"){
        val show = call.parameters["show"] ?: return@get call.respondText("Missing show.", status = HttpStatusCode.BadRequest)
        val season = call.request.queryParameters["season"]
        val quotes = quotesDAO.quotesByShow(show, season?.toInt())
        call.respond(quotes)
//        if(season == null){
//            val quotes = quotesStorage.filter { it.show == show } ?: return@get call.respondText("No show with name $show", status = HttpStatusCode.NotFound)
//            call.respond(quotes)
//        }
//        else {
//            val quotes = quotesStorage.filter { it.show == show && it.season == season.toInt() } ?: call.respondText("No show with name $show", status = HttpStatusCode.NotFound)
//            call.respond(quotes)
//        }
    }
}

fun Route.createQuote(){
    post("/quote"){
        // With generic parameter, it automatically deserializes the JSON request body into Quote object.
        val quote = call.receive<Quote>()
//        quotesStorage.add(quote)
        val createdQuote = quotesDAO.addQuote(show=quote.show, season = quote.season, episode = quote.episode, character = quote.character, quote = quote.quote )
//       201 Created
        call.respondText("Quote stored correctly.", status = HttpStatusCode.Created)
    }

}

fun Route.deleteQuote(){
    delete("/quote/{id}") {
//        return statement means nothing below will process
        val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
//        if(quotesStorage.removeIf { it.id == id.toInt()}){
//// 202 Accepted
//            call.respondText("Quote removed correctly.", status = HttpStatusCode.Accepted)
//        }
        if(quotesDAO.removeQuote(id.toInt())){
            call.respondText("Quote removed correctly.", status = HttpStatusCode.Accepted)
        }
        else {
//            404 not found
            call.respondText("Not found.", status = HttpStatusCode.NotFound)
        }
    }
}

fun Route.editQuote(){
    put("/quote/{id}"){
        val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
        val editedQuote = call.receive<Quote>()
        // Remember that with PUT the JSON body contains the complete new state of the resource, even if you're only updating a few fields but ID is fine to be as path parameter, no need for duplication
        if(quotesDAO.editQuote(id = id.toInt(), show = editedQuote.show, season = editedQuote.season, episode = editedQuote.episode, character = editedQuote.character, quote = editedQuote.quote)){
            call.respondText("Quote updated correctly.", status = HttpStatusCode.OK)
        }
        else {
            return@put call.respondText("Not found", status = HttpStatusCode.NotFound)
        }
//        val quoteToUpdate = quotesStorage.find {it.id == id.toInt()} ?:
//        val indexOfQuote = quotesStorage.indexOf(quoteToUpdate)
//        quotesStorage[indexOfQuote] = call.receive<Quote>()
//        200 OK
    }
}

