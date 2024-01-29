package com.api.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Character(val id: Int, val name: String, val description: String)

object Characters : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 20)
    val description = varchar("description", length = 1024)

    override val primaryKey = PrimaryKey(id)

}
val charactersStorage = mutableListOf<Character>(
    Character(id=1, name = "The Mandalorian", description = "The Mandalorian, also known as Mando or Din Djarin, is a bounty hunter navigating the outer rim of the galaxy in the era after the fall of the Galactic Empire. He unexpectedly becomes the guardian of Grogu, a young Force-sensitive being. He forms a strong bond with Grogu, despite his initial reluctance and conflicting Mandalorian beliefs."),
    Character(id=2, name = "Grogu", description = "Grogu is a young Force-sensitive being that belongs to the same species as Yoda. He was found by The Mandalorian during a bounty hunt on Arvala-7. Grogu forms an unlikely bond with The Mandalorian, becoming his adopted son. Together, they navigate the dangers of the outer rim, facing bounty hunters, Imperial remnants, and other threats."),
    Character(id=3, name="The Armorer", description = "The Armorer is the leader of a hidden Mandalorian tribe called the Children of the Watch. She crafts and repairs beskar armor and embodies the ancient Mandalorian code and upholds strict traditions. She serves as a moral compass, challenging The Mandalorian and pushing him to confront his inner conflicts."),
    Character(id=4, name = "Bo-Katan Kryze", description = "Bo-Katan Kryze is a Mandalorian princess. She was a lieutenant in the Death Watch group and leader of the Mandalore resistance during the Clone Wars. She met The Mandalorian while searching for Moff Gideon and the Darksaber. The two eventually teamed up with their respective tribes and retook the planet of Mandalore."),
    Character(id=5, name="Greef Karga", description = "Initially, Greef Karga, was the Guild Master of Bounty Hunters on Nevarro and tried to kill the Mandalorian after he saved Grogu from the Empire. However, after Grogu saved his life, he helped The Mandalorian defeat Moff Gideon and remove the Imperials from Nevarro. Since then he has turned Nevarro into a trade outpost. He provides crucial advice and resources to The Mandalorian and is a staunch defender of Grogu.")
)

//addCharacter( "Captain Bombardier")
//addCharacter( "Cad Bane")
//addCharacter( "Cara Dune")
//addCharacter( "Boba Fett")
//addCharacter( "Frog Lady")
//addCharacter( "Bib Fortuna")
//addCharacter( "Garsa Fwip")
//addCharacter( "Moff Gideon")
//addCharacter("IG-11")
//addCharacter( "Greef Karga")
