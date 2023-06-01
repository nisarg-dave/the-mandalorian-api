package com.api.dao.characters

import com.api.models.*

interface CharactersDaoFacade {
    suspend fun getCharacter(characterName: String): Character?
    suspend fun addCharacter(characterName: String): Character?
}