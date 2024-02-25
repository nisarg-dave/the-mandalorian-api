package com.api.routes

import at.favre.lib.crypto.bcrypt.BCrypt
import com.api.dao.users.userDao
import com.api.models.UserContent
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import java.lang.Exception
import java.util.*

fun Application.authRoute(){
//  Can access environment because in the scope of application
    val config = environment.config
    routing {
        route("/auth"){
            getToken(config)
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
fun Route.getToken(config: ApplicationConfig){
    post("/token"){
        try{
            val userReceived = call.receive<UserContent>()
            if(userReceived.username.isEmpty()) call.respondText("Username can't be empty.", status = HttpStatusCode.BadRequest)
            if(userReceived.password.isEmpty()) call.respondText("Password can't be empty.", status = HttpStatusCode.BadRequest)
            val userFound = userDao.findUserByUsername(userReceived.username)
            if(userFound != null){
                val hashedPassword = BCrypt.withDefaults().hashToString(12, userReceived.password.toCharArray())
//              Verifying if the password in application.conf is the same as the hashed password
                val result = BCrypt.verifyer().verify(config.property("admin-credentials.password").getString().toCharArray(), hashedPassword)
                if(result.verified){
                    val secret = config.property("jwt.secret").getString()
                    val issuer = config.property("jwt.issuer").getString()
                    val audience = config.property("jwt.audience").getString()
                    val token = JWT.create()
                        .withAudience(audience)
                        .withIssuer(issuer)
                        .withClaim("username", userFound.username)
                        .withClaim("admin", true)
                        .withExpiresAt(Date(System.currentTimeMillis() + 60 * 60 * 1000))
                        .sign(Algorithm.HMAC256(secret))
                    call.respond(hashMapOf("token" to token))
                }
                else{
                    call.respondText("Invalid credentials.", status=HttpStatusCode.Unauthorized)
                }
            }
            else{
                call.respondText("Invalid credentials.", status=HttpStatusCode.Unauthorized)
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