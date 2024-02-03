package com.api.dao.planets

import com.api.dao.DatabaseFactory.dbQuery
import com.api.models.Planet
import com.api.models.Planets
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.ResultRow
import kotlin.random.Random
import kotlin.random.nextInt
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq




class PlanetsDaoFacadeImpl : PlanetsDaoFacade {
    private fun resultRowToPlanet(row: ResultRow):Planet = Planet(
        id = row[Planets.id],
        name = row[Planets.name],
        description = row[Planets.description]
    )

    override suspend fun randomPlanet(): Planet? {
        return dbQuery{
            val randomNumber = Random.nextInt(1..3)
            Planets.select{Planets.id eq randomNumber}.map(::resultRowToPlanet).singleOrNull()
        }
    }

    override suspend fun planetByName(planetName: String): Planet? {
        return dbQuery {
            Planets.select {Planets.name eq planetName}.map(::resultRowToPlanet).singleOrNull()
        }
    }

    override suspend fun addPlanet(name: String, description: String): Planet? {
        return dbQuery {
            val insertStatement = Planets.insert {
                it[this.name] = name
                it[this.description] = description
            }
            insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToPlanet)
        }
    }

    override suspend fun removePlanet(id: Int): Boolean {
        return dbQuery {
            Planets.deleteWhere { Planets.id eq id }
        } > 0
    }

    override suspend fun editPlanet(id: Int, name: String, description: String): Boolean {
        return dbQuery {
            Planets.update({Planets.id eq id}){
                it[this.name] = name
                it[this.description] = description
            }
        } > 0
    }
}

val planetsDao = PlanetsDaoFacadeImpl().apply {
    runBlocking {
        addPlanet(name="Pagodon", description = "Pagodon is a ice-covered planet located in the Outer Rim and is the home of ravinak creatures. The Mandalorian arrived on Pagodon in season 1 and captured a Mythrol at the bar after some resistance.")
        addPlanet(name="Nevarro", description = "Nevarro is a volcanic planet located in the Outer Rim. It is known for its lava fields and volcanic rivers. In season 1, Nevarro was where the Bounty Hunter's guild operated from under Greef Karga. By season 3, it became a trade outpost.")
        addPlanet(name="Aq Vetina", description = "Aq Vetina is the home planet of The Mandalorian and is located in the Outer Rim. During the Clone Wars, the Mandalorians of the Death Watch group rescued a young Din Djarin from Separatist droids.")
    }
}