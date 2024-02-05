package com.api.dao.characters

import com.api.models.Character
import com.api.models.Characters
import com.api.dao.DatabaseFactory.dbQuery
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlin.random.Random
import kotlin.random.nextInt


class CharactersDaoFacadeImpl : CharactersDaoFacade {
    private fun resultRowToCharacter(row: ResultRow): Character = Character(
        id = row[Characters.id],
        name = row[Characters.name],
        description = row[Characters.description]
    )

    override suspend fun randomCharacter(): Character? {
        return dbQuery {
            val randomNumber = Random.nextInt(1..5)
            Characters.select {Characters.id eq randomNumber}.map(::resultRowToCharacter).singleOrNull()
        }
    }

    override suspend fun characterByName(characterName: String): Character? {
        return dbQuery {
            Characters.select {Characters.name eq characterName}.map(::resultRowToCharacter).singleOrNull()
        }
    }

    override suspend fun addCharacter(name: String, description: String): Character? {
        return dbQuery {
            val insertStatement = Characters.insert {
                it[this.name] = name
                it[this.description] = description
            }
            insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToCharacter)
        }
    }

    override suspend fun removeCharacter(id: Int): Boolean {
        return dbQuery {
            Characters.deleteWhere {Characters.id eq id}
        } > 0
    }

    override suspend fun editCharacter(id: Int, name: String, description: String): Boolean {
        return dbQuery {
            Characters.update({Characters.id eq id}){
                it[this.name] = name
                it[this.description] = description
            }
        } > 0
    }
}

val charactersDAO = CharactersDaoFacadeImpl().apply {
    runBlocking {
        addCharacter(name = "The Mandalorian", description = "The Mandalorian, also known as Mando or Din Djarin, is a bounty hunter navigating the outer rim of the galaxy in the era after the fall of the Galactic Empire. He unexpectedly becomes the guardian of Grogu, a young Force-sensitive being. He forms a strong bond with Grogu, despite his initial reluctance and conflicting Mandalorian beliefs.")
        addCharacter(name = "Grogu", description = "Grogu is a young Force-sensitive being that belongs to the same species as Yoda. He was found by The Mandalorian during a bounty hunt on Arvala-7. Grogu forms an unlikely bond with The Mandalorian, becoming his adopted son. Together, they navigate the dangers of the outer rim, facing bounty hunters, Imperial remnants, and other threats.")
        addCharacter(name="The Armorer", description = "The Armorer is the leader of a hidden Mandalorian tribe called the Children of the Watch. She crafts and repairs beskar armor and embodies the ancient Mandalorian code and upholds strict traditions. She serves as a moral compass, challenging The Mandalorian and pushing him to confront his inner conflicts.")
        addCharacter(name = "Bo-Katan Kryze", description = "Bo-Katan Kryze is a Mandalorian princess. She was a lieutenant in the Death Watch group and leader of the Mandalore resistance during the Clone Wars. She met The Mandalorian while searching for Moff Gideon and the Darksaber. The two eventually teamed up with their respective tribes and retook the planet of Mandalore.")
        addCharacter(name="Greef Karga", description = "Initially, Greef Karga, was the Guild Master of Bounty Hunters on Nevarro and tried to kill the Mandalorian after he saved Grogu from the Empire. However, after Grogu saved his life, he helped The Mandalorian defeat Moff Gideon and remove the Imperials from Nevarro. Since then he has turned Nevarro into a trade outpost. He provides crucial advice and resources to The Mandalorian and is a staunch defender of Grogu.")
        addCharacter(name="Kuiil", description = "")
        addCharacter(name="IG-11", description = "")
        addCharacter(name="Fennec Shand", description = "")
        addCharacter(name="Cobb Vanth", description = "")
        addCharacter(name="Cara Dune", description = "")
        addCharacter(name="Ahsoka Tano", description = "")
        addCharacter(name="Boba Fett", description = "")
        addCharacter(name="Moff Gideon", description = "")
        addCharacter(name="Luke Skywalker", description = "")
        addCharacter(name="Hutt Twin", description = "")
        addCharacter(name="Garsa Fwip", description = "")
        addCharacter(name="Peli Motto", description = "")
        addCharacter(name="Cad Bane", description = "")
        addCharacter(name="Ezra Bridger", description = "")
        addCharacter(name="Hera Syndulla", description = "")
        addCharacter(name="Baylan Skoll", description = "")
        addCharacter(name="Morgan Elsbeth", description = "")
        addCharacter(name="Huyang", description = "")
        addCharacter(name="Anakin Skywalker", description = "")
        addCharacter(name="Great Mothers", description = "")
        addCharacter(name="Thrawn", description = "")
        addCharacter(name="Enoch", description = "")


//        addCharacter( "Captain Bombardier")
//        addCharacter( "Cad Bane")
//        addCharacter( "Cara Dune")
//        addCharacter( "Boba Fett")
//        addCharacter( "Frog Lady")
//        addCharacter( "Bib Fortuna")
//        addCharacter( "Garsa Fwip")
//        addCharacter( "Moff Gideon")
//        addCharacter("IG-11")
    }
}