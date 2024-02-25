package com.api.routes

import com.api.dao.characters.charactersDAO
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

fun Application.characterRoutes(){
    routing {
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
    }
}

fun Route.getCharacterByName(){
    get("/character/{name}"){
        val name = call.parameters["name"] ?: return@get call.respondText("Missing name.", status = HttpStatusCode.BadRequest)
        val character = charactersDAO.characterByName(name) ?: return@get call.respondText("Not found.", status=HttpStatusCode.NotFound)
        call.respond(character)
    }
}

@OptIn(ExperimentalSerializationApi::class)
fun Route.createCharacter(){
    authenticate("auth-jwt") {
        post("/character") {
            try{
                val character = call.receive<CharacterContent>()
//              Validation
                if(character.name.isEmpty()) call.respondText("Character name can't be empty.", status = HttpStatusCode.BadRequest)
                if(character.description.isEmpty()) call.respondText("Description can't be empty.", status = HttpStatusCode.BadRequest)
                if(character.imgUrl.isEmpty()) call.respondText("Image URL can't be empty.", status = HttpStatusCode.BadRequest)

                val createdCharacter = charactersDAO.addCharacter(
                    name = character.name,
                    description = character.description,
                    imgUrl = character.imgUrl
                )
                if (createdCharacter != null) {
                    call.respond(status = HttpStatusCode.Created, createdCharacter)
                } else {
                    call.respondText("Failed to store Character correctly.", status = HttpStatusCode.InternalServerError)
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
                call.respondText("An unexpected error occurred.", status = HttpStatusCode.InternalServerError)
            }
        }
    }
}

fun Route.deleteCharacter(){
    authenticate("auth-jwt") {
        delete("/character/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (charactersDAO.removeCharacter(id.toInt())) {
                call.respondText("Character removed correctly.", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not found.", status = HttpStatusCode.NotFound)
            }
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
fun Route.editCharacter(){
    authenticate("auth-jwt") {
        put("/character/{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)

            try{
                val characterToUpdate = call.receive<Character>()
//              Validation
                if(characterToUpdate.name.isEmpty()) call.respondText("Character name can't be empty.", status = HttpStatusCode.BadRequest)
                if(characterToUpdate.description.isEmpty()) call.respondText("Description can't be empty.", status = HttpStatusCode.BadRequest)
                if(characterToUpdate.imgUrl.isEmpty()) call.respondText("Image URL can't be empty.", status = HttpStatusCode.BadRequest)

                if (charactersDAO.editCharacter(
                        id = id.toInt(),
                        name = characterToUpdate.name,
                        description = characterToUpdate.description,
                        imgUrl = characterToUpdate.imgUrl
                    )
                ) {
                    call.respondText("Character updated correctly.", status = HttpStatusCode.OK)
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
                call.respondText("An unexpected error occurred.", status = HttpStatusCode.InternalServerError)
            }
        }
    }
}