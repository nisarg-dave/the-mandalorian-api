package com.api.dao.planets

import com.api.models.Planet

interface PlanetsDaoFacade {
    suspend fun randomPlanet(): Planet?
    suspend fun planetByName(planetName: String): Planet?
    suspend fun addPlanet(name: String, description: String, imgUrl: String): Planet?
    suspend fun removePlanet(id: Int): Boolean
    suspend fun editPlanet(id:Int, name: String, description: String, imgUrl: String): Boolean
}