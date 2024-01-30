package com.api.routes

import com.api.models.*
import io.ktor.server.application.*
import io.ktor.http.*
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
        if(planetsStorage.isNotEmpty()){
            val randomNumber = Random.nextInt(1..3)
            call.respond(planetsStorage[randomNumber])
        }
        else{
            call.respondText("No planet storage found.", status = HttpStatusCode.OK)
        }
    }
}

fun Route.getPlanetByName(){
    get("/planet/{name}"){
        val name = call.parameters["name"] ?: return@get call.respondText("Missing name.", status = HttpStatusCode.BadRequest)
        val planet = planetsStorage.find {it.name == name} ?: return@get call.respondText("Not found", status=HttpStatusCode.NotFound)
        call.respond(planet)
    }
}

fun Route.createPlanet(){
    post("/planet"){
        val planet = call.receive<Planet>()
        planetsStorage.add(planet)
        call.respondText("Planet stored correctly.", status = HttpStatusCode.Created)
    }

}

fun Route.deletePlanet(){
    delete("/planet/{id}") {
        val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
        if(planetsStorage.removeIf {it.id == id.toInt()}){
            call.respondText("Planet removed correctly.", status = HttpStatusCode.Accepted)
        }
        else{
            call.respondText("Not found.", status = HttpStatusCode.NotFound)
        }
    }
}

fun Route.editPlanet(){
    put("/planet/{id}"){
        val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
        val planetToUpdate = planetsStorage.find { it.id == id.toInt() }
        val indexOfPlanet = planetsStorage.indexOf(planetToUpdate)
        planetsStorage[indexOfPlanet] = call.receive<Planet>()
        call.respondText("Planet updated correctly.", status = HttpStatusCode.OK)
    }
}



