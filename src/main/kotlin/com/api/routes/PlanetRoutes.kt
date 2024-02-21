package com.api.routes

import com.api.dao.planets.planetsDao
import com.api.models.*
import io.ktor.server.application.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.random.Random
import kotlin.random.nextInt

fun Application.planetRoutes(){
    // Take trailing lambda as a parameter
    routing {
        //        The routing function establishes a context where an implicit Route receiver is available within its lambda.
        getRandomPlanet()
        getPlanetByName()
        createPlanet()
        deletePlanet()
        editPlanet()
    }
}

fun Route.getRandomPlanet(){
    get("/random/planet"){
//        if(planetsStorage.isNotEmpty()){
//            val randomNumber = Random.nextInt(1..3)
//            call.respond(planetsStorage[randomNumber])
//        }
//        else{
//            call.respondText("No planet storage found.", status = HttpStatusCode.OK)
//        }
        call.respond(planetsDao.randomPlanet() ?: call.respondText("No planet storage found.", status = HttpStatusCode.OK))
    }
}

fun Route.getPlanetByName(){
    get("/planet/{name}"){
        val name = call.parameters["name"] ?: return@get call.respondText("Missing name.", status = HttpStatusCode.BadRequest)
//        val planet = planetsStorage.find {it.name == name} ?: return@get call.respondText("Not found", status=HttpStatusCode.NotFound)
        val planet = planetsDao.planetByName(name) ?: return@get call.respondText("Not found.", status=HttpStatusCode.NotFound)
        call.respond(planet)
    }
}

fun Route.createPlanet(){
    authenticate("auth-jwt") {
        post("/planet") {
            val planet = call.receive<PlanetContent>()
            if(planet.name.isEmpty()) call.respondText("Planet name can't be empty.", status = HttpStatusCode.BadRequest)
            if(planet.description.isEmpty()) call.respondText("Description can't be empty.", status = HttpStatusCode.BadRequest)
            if(planet.imgUrl.isEmpty()) call.respondText("Image URL can't be empty.", status = HttpStatusCode.BadRequest)
//        planetsStorage.add(planet)
            val createdPlanet =
                planetsDao.addPlanet(name = planet.name, description = planet.description, imgUrl = planet.imgUrl)
            if (createdPlanet != null) {
                call.respond(status = HttpStatusCode.Created, createdPlanet)
            } else {
                call.respondText("Failed to store Planet correctly.", status = HttpStatusCode.InternalServerError)
            }
        }
    }
}

fun Route.deletePlanet(){
    authenticate("auth-jwt") {
        delete("/planet/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
//        if(planetsStorage.removeIf {it.id == id.toInt()}){
//            call.respondText("Planet removed correctly.", status = HttpStatusCode.Accepted)
//        }
//        else{
//            call.respondText("Not found.", status = HttpStatusCode.NotFound)
//        }
            if (planetsDao.removePlanet(id.toInt())) {
                call.respondText("Planet removed correctly.", status = HttpStatusCode.Accepted)
            } else {
                call.respondText("Not found.", status = HttpStatusCode.NotFound)
            }
        }
    }
}

fun Route.editPlanet(){
    authenticate("auth-jwt") {
        put("/planet/{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
//        val planetToUpdate = planetsStorage.find { it.id == id.toInt() }
//        val indexOfPlanet = planetsStorage.indexOf(planetToUpdate)
//        planetsStorage[indexOfPlanet] = call.receive<Planet>()
            val planetToUpdate = call.receive<Planet>()
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
    }
}



