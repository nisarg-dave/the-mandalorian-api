package com.api.routes

import com.api.dao.characters.charactersDAO
import com.api.models.*
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getCharacter(){
    get("/character"){
        val name = call.request.queryParameters["name"] ?: call.respondText("Please Provide A Name", status = HttpStatusCode.BadRequest)
        val character = charactersDAO.getCharacter(call.request.queryParameters["name"]!!) ?: call.respondText("Character Not Found!", status = HttpStatusCode.NotFound)
        call.respond(character)
    }
}