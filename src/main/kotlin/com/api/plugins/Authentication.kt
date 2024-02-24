package com.api.plugins

import com.auth0.jwt.JWT
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.response.*


fun Application.configureAuthentication(){
    //   Can access environment.config because the block is in the scope of the Application class
    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()
    val myRealm = environment.config.property("jwt.realm").getString()

    install(Authentication){
//        A function for using a specific provider optionally allows you to specify a provider name.
        jwt("auth-jwt") {
            realm = myRealm
//          Verifies a token's format and its signature
            verifier(
                JWT
                .require(Algorithm.HMAC256(secret))
                .withAudience(audience)
                .withIssuer(issuer)
                .build())
//          Performing additional validations on the JWT payload.
            validate { credential ->
                if(credential.payload.getClaim("username").asString() != "" && credential.payload.getClaim("admin").asBoolean()){
                    JWTPrincipal(credential.payload)
                }
                else{
                    null
                }
            }
//           Response sent if authentication fails.
            challenge{ _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}