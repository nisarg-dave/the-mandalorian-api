package com.api.plugins

import com.auth0.jwt.JWT
import io.ktor.server.config.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.response.*


fun Application.configureAuthentication(){
    //   Can access environment because in the scope of application
    val secret = environment.config.property("jwt.secret").getString()
    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()
    val myRealm = environment.config.property("jwt.realm").getString()
//    Install is a function defined inside a class and can only be called from the scope of its class and same with jwt can only be called in AuthenticationConfig
    install(Authentication){
//        A function for using a specific provider optionally allows you to specify a provider name.
        jwt("auth-jwt") {
            realm = myRealm
//          Verification and validation happens when we receive a token in request body.
//          The verifier function allows you to verify a token format and its signature
            verifier(
                JWT
                .require(Algorithm.HMAC256(secret))
                .withAudience(audience)
                .withIssuer(issuer)
                .build())
//          The validate function allows you to perform additional validations on the JWT payload.
//          Credential is a set of properties for a server to authenticate a principal: a user/password pair, an API key, and so on. JWT credntials consist of payload
//          A principal is an entity that can be authenticated: a user, a computer, a service, etc. JWTPrincipal consists of specified payload
            validate { credential ->
                if(credential.payload.getClaim("username").asString() != "" && credential.payload.getClaim("admin").asBoolean()){
                    JWTPrincipal(credential.payload)
                }
                else{
                    null
                }
            }
//           The challenge function allows you to configure a response to be sent if authentication fails.
            challenge{ _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}