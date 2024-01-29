package com.api.routes

import com.api.dao.characters.charactersDAO
import com.api.models.*
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.random.Random
import kotlin.random.nextInt


fun Route.getRandomCharacter(){
    get("/random/character"){
        if(charactersStorage.isNotEmpty()){
            val randomNumber = Random.nextInt(1..4)
            call.respond(charactersStorage[randomNumber])
        }
        else{
            call.respondText("No character storage found.", status = HttpStatusCode.OK)
        }
    }
}

fun Route.getCharacterByName(){
    get("/character/{name}"){
        val name = call.parameters["name"] ?: return@get call.respondText("Missing name.", status=HttpStatusCode.BadRequest)
        val character = charactersStorage.find {it.name == name} ?: return@get call.respondText("Not found", status=HttpStatusCode.NotFound)
        call.respond(character)
    }
}


// character/name
// PUT /character/id
// POSt /character
// delete /character/id