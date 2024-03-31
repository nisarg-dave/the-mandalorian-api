package com.api.routes

import com.api.dao.quotes.quotesDAO
import com.api.models.*
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import java.lang.Exception

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
    get("/random/quote"){
        call.respond(quotesDAO.randomQuote() ?: call.respondText("No quotes storage found.", status = HttpStatusCode.OK))
    }
}

fun Route.getQuotesByCharacter(){
    get("/quotes/character/{character}"){
        val character  = call.parameters["character"] ?: return@get call.respondText("Missing character.", status = HttpStatusCode.BadRequest)
        val quotesByCharacter = quotesDAO.quotesByCharacter(character)
        if(quotesByCharacter.isEmpty()){
            call.respondText("Not Found", status = HttpStatusCode.NotFound)
        }
        else {
            call.respond(quotesByCharacter)
        }
    }
}

fun Route.getQuotesByShow(){
    get("/quotes/show/{show}"){
        val show = call.parameters["show"] ?: return@get call.respondText("Missing show.", status = HttpStatusCode.BadRequest)
        val season = call.request.queryParameters["season"]
        val quotes = quotesDAO.quotesByShow(show, season?.toInt())
        if(quotes.isEmpty()) {
            call.respondText("Not Found", status = HttpStatusCode.NotFound)
        }
        else {
            call.respond(quotes)
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
fun Route.createQuote(){
//  Providing name of provider
    authenticate("auth-jwt") {
        post("/quote") {
            try{
                val quote = call.receive<QuoteContent>()
                // Validation
                if(quote.show.isEmpty()) return@post call.respondText("Show can't be empty.", status = HttpStatusCode.BadRequest)
                if(quote.episode.isEmpty()) return@post call.respondText("Episode can't be empty.", status = HttpStatusCode.BadRequest)
                if(quote.character.isEmpty()) return@post call.respondText("Character can't be empty.", status = HttpStatusCode.BadRequest)
                if(quote.quote.isEmpty()) return@post call.respondText("Quote can't be empty.", status = HttpStatusCode.BadRequest)

                val createdQuote = quotesDAO.addQuote(
                    show = quote.show,
                    season = quote.season,
                    episode = quote.episode,
                    character = quote.character,
                    quote = quote.quote
                )
                if (createdQuote != null) {
                    call.respond(status = HttpStatusCode.Created, createdQuote)
                } else {
                    call.respondText("Failed to store Quote correctly.", status = HttpStatusCode.InternalServerError)
                }
            }
            catch(e: BadRequestException){
                call.respondText(e.message!!, status = HttpStatusCode.BadRequest)
            }
            catch(e: JsonConvertException){
                call.respondText(e.message!!, status = HttpStatusCode.BadRequest)
            }
            catch(e: MissingFieldException){
                call.respondText(e.message!!, status = HttpStatusCode.BadRequest)
            }
            catch(e: Exception){
                call.respondText(e.message!!, status = HttpStatusCode.InternalServerError)
            }
        }
    }
}

fun Route.deleteQuote(){
    authenticate("auth-jwt") {
        delete("/quote/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (quotesDAO.removeQuote(id.toInt())) {
                call.respondText("Quote removed correctly.", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not found.", status = HttpStatusCode.NotFound)
            }
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
fun Route.editQuote(){
    authenticate("auth-jwt") {
        put("/quote/{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            try{
                val quoteToUpdate = call.receive<Quote>()
                // Validation
                if(quoteToUpdate.show.isEmpty()) return@put call.respondText("Show can't be empty.", status = HttpStatusCode.BadRequest)
                if(quoteToUpdate.episode.isEmpty()) return@put call.respondText("Episode can't be empty.", status = HttpStatusCode.BadRequest)
                if(quoteToUpdate.character.isEmpty()) return@put call.respondText("Character can't be empty.", status = HttpStatusCode.BadRequest)
                if(quoteToUpdate.quote.isEmpty()) return@put call.respondText("Quote can't be empty.", status = HttpStatusCode.BadRequest)

                if (quotesDAO.editQuote(
                        id = id.toInt(),
                        show = quoteToUpdate.show,
                        season = quoteToUpdate.season,
                        episode = quoteToUpdate.episode,
                        character = quoteToUpdate.character,
                        quote = quoteToUpdate.quote
                    )
                ) {
                    call.respondText("Quote updated correctly.", status = HttpStatusCode.OK)
                } else {
                    call.respondText("Not found.", status = HttpStatusCode.NotFound)
                }
            }
            catch(e: BadRequestException){
                call.respondText(e.message!!, status = HttpStatusCode.BadRequest)
            }
            catch(e: JsonConvertException){
                call.respondText(e.message!!, status = HttpStatusCode.BadRequest)
            }
            catch(e: MissingFieldException){
                call.respondText(e.message!!, status = HttpStatusCode.BadRequest)
            }
            catch(e: Exception){
                call.respondText(e.message!!, status = HttpStatusCode.InternalServerError)
            }
        }
    }
}