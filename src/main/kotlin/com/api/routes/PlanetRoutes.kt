package com.api.routes

import com.api.dao.planets.planetsDao
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

fun Application.planetRoutes(){
    routing {
        getRandomPlanet()
        getPlanetByName()
        createPlanet()
        deletePlanet()
        editPlanet()
    }
}

fun Route.getRandomPlanet(){
    get("/random/planet"){
        call.respond(planetsDao.randomPlanet() ?: call.respondText("No planet storage found.", status = HttpStatusCode.OK))
    }
}

fun Route.getPlanetByName(){
    get("/planet/{name}"){
        val name = call.parameters["name"] ?: return@get call.respondText("Missing name.", status = HttpStatusCode.BadRequest)
        val planet = planetsDao.planetByName(name) ?: return@get call.respondText("Not found.", status=HttpStatusCode.NotFound)
        call.respond(planet)
    }
}

@OptIn(ExperimentalSerializationApi::class)
fun Route.createPlanet(){
    authenticate("auth-jwt") {
        post("/planet") {
            try{
                val planet = call.receive<PlanetContent>()
//              Validation
                if(planet.name.isEmpty()) call.respondText("Planet name can't be empty.", status = HttpStatusCode.BadRequest)
                if(planet.description.isEmpty()) call.respondText("Description can't be empty.", status = HttpStatusCode.BadRequest)
                if(planet.imgUrl.isEmpty()) call.respondText("Image URL can't be empty.", status = HttpStatusCode.BadRequest)

                val createdPlanet =
                    planetsDao.addPlanet(name = planet.name, description = planet.description, imgUrl = planet.imgUrl)
                if (createdPlanet != null) {
                    call.respond(status = HttpStatusCode.Created, createdPlanet)
                } else {
                    call.respondText("Failed to store Planet correctly.", status = HttpStatusCode.InternalServerError)
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
//                call.respondText("An unexpected error occurred.", status = HttpStatusCode.InternalServerError)
            }
        }
    }
}

fun Route.deletePlanet(){
    authenticate("auth-jwt") {
        delete("/planet/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            if (planetsDao.removePlanet(id.toInt())) {
                call.respondText("Planet removed correctly.", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not found.", status = HttpStatusCode.NotFound)
            }
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
fun Route.editPlanet(){
    authenticate("auth-jwt") {
        put("/planet/{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            try{
                val planetToUpdate = call.receive<Planet>()
//              Validation
                if(planetToUpdate.name.isEmpty()) call.respondText("Planet name can't be empty.", status = HttpStatusCode.BadRequest)
                if(planetToUpdate.description.isEmpty()) call.respondText("Description can't be empty.", status = HttpStatusCode.BadRequest)
                if(planetToUpdate.imgUrl.isEmpty()) call.respondText("Image URL can't be empty.", status = HttpStatusCode.BadRequest)

                if (planetsDao.editPlanet(
                        id = id.toInt(),
                        name = planetToUpdate.name,
                        description = planetToUpdate.description,
                        imgUrl = planetToUpdate.imgUrl
                    )
                ) {
                    call.respondText("Planet updated correctly.", status = HttpStatusCode.OK)
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