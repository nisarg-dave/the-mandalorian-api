package com.api.dao.users

import com.api.models.User

interface UsersDaoFacade {
    suspend fun findUserByUsername(username: String): User?
}