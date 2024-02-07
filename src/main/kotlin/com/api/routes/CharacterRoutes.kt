package com.api.routes

import com.api.dao.characters.charactersDAO
import com.api.models.*
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.random.Random
import kotlin.random.nextInt

fun Application.characterRoutes(){
    // Take trailing lambda as a parameter
    routing {
        //        The routing function establishes a context where an implicit Route receiver is available within its lambda.
        getRandomCharacter()
        getCharacterByName()
        createCharacter()
        deleteCharacter()
        editCharacter()
    }
}

fun Route.getRandomCharacter(){
    get("/random/character"){
        call.respond(charactersDAO.randomCharacter() ?: call.respondText("No character storage found.", status = HttpStatusCode.OK))
//        if(charactersStorage.isNotEmpty()){
//            val randomNumber = Random.nextInt(1..4)
//            call.respond(charactersStorage[randomNumber])
//        }
//        else{
//            call.respondText("No character storage found.", status = HttpStatusCode.OK)
//        }
    }
}

fun Route.getCharacterByName(){
    get("/character/{name}"){
        val name = call.parameters["name"] ?: return@get call.respondText("Missing name.", status = HttpStatusCode.BadRequest)
//        val character = charactersStorage.find {it.name == name} ?: return@get call.respondText("Not found", status=HttpStatusCode.NotFound)
        val character = charactersDAO.characterByName(name) ?: return@get call.respondText("Not found.", status=HttpStatusCode.NotFound)
        call.respond(character)
    }
}

fun Route.createCharacter(){
    post("/character"){
        val character = call.receive<CharacterPostBody>()
//        charactersStorage.add(character)
        val createdCharacter = charactersDAO.addCharacter(name=character.name, description = character.description)
        if(createdCharacter != null) {
            call.respond(status = HttpStatusCode.Created, createdCharacter)
        }
        else{
            call.respondText("Failed to store Character correctly.", status = HttpStatusCode.InternalServerError)
        }
    }
}

fun Route.deleteCharacter(){
    delete("/character/{id}") {
        val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
        if(charactersDAO.removeCharacter(id.toInt())){
            call.respondText("Character removed correctly.", status = HttpStatusCode.Accepted)
        }
        else{
            call.respondText("Not found.", status = HttpStatusCode.NotFound)
        }
    }
}

fun Route.editCharacter(){
    put("/character/{id}"){
        val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
//        val characterToUpdate = charactersStorage.find { it.id == id.toInt() }
//        val indexOfCharacter = charactersStorage.indexOf(characterToUpdate)
//        charactersStorage[indexOfCharacter] = call.receive<Character>()
        val characterToUpdate = call.receive<Character>()
        if(charactersDAO.editCharacter(id =  id.toInt(), name = characterToUpdate.name, description = characterToUpdate.description)){
            call.respondText("Character updated correctly.", status = HttpStatusCode.OK)
        }
        else{
            call.respondText("Not found.", status=HttpStatusCode.NotFound)
        }
    }
}



