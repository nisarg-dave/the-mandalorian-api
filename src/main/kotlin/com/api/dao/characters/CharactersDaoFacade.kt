package com.api.dao.characters

import com.api.models.Character

interface CharactersDaoFacade {
    suspend fun randomCharacter(): Character?
    suspend fun characterByName(characterName: String): Character?
    suspend fun addCharacter(name: String, description: String, imgUrl: String): Character?
    suspend fun removeCharacter(id: Int): Boolean
    suspend fun editCharacter(id: Int, name: String, description: String, imgUrl: String): Boolean
}