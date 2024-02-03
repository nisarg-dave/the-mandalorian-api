package com.api.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Planet(val id: Int, val name: String, val description: String)

object Planets : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 20)
    val description = varchar("description", length = 1024)

    override val primaryKey = PrimaryKey(id)

}

//val planetsStorage = mutableListOf(
//    Planet(id=1, name="Pagodon", description = "Pagodon is a ice-covered planet located in the Outer Rim and is the home of ravinak creatures. The Mandalorian arrived on Pagodon in season 1 and captured a Mythrol at the bar after some resistance."),
//    Planet(id=2, name="Nevarro", description = "Nevarro is a volcanic planet located in the Outer Rim. It is known for its lava fields and volcanic rivers. In season 1, Nevarro was where the Bounty Hunter's guild operated from under Greef Karga. By season 3, it became a trade outpost."),
//Planet(id=3, name="Aq Vetina", description = "Aq Vetina is the home planet of The Mandalorian and is located in the Outer Rim. During the Clone Wars, the Mandalorians of the Death Watch group rescued a young Din Djarin from Separatist droids."))