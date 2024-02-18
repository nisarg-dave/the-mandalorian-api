package com.api.routes

import at.favre.lib.crypto.bcrypt.BCrypt
import com.api.dao.users.userDao
import com.api.models.UserContent
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.authRoute(){
    routing {
        route("/auth"){
            getToken()
        }
    }
}

fun Route.getToken(){
    post("/token"){
        val userReceived = call.receive<UserContent>()
        val userFound = userDao.findUserByUsername(userReceived.username)
        if(userFound != null){
            val hashedPassword = BCrypt.withDefaults().hashToString(12, userReceived.password.toCharArray())
            val result = BCrypt.verifyer().verify(userFound.password.toCharArray(), hashedPassword)
            if(result.verified){
                call.respondText("Token")
            }
            else{
                call.respondText("Invalid credentials.", status=HttpStatusCode.Unauthorized)
            }
        }
        else{
            call.respondText("Invalid credentials.", status=HttpStatusCode.Unauthorized)
        }
    }
}