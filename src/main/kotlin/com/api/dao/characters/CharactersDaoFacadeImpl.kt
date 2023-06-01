package com.api.dao.characters

import com.api.models.Character
import com.api.models.Characters
import com.api.dao.DatabaseFactory.dbQuery
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class CharactersDaoFacadeImpl : CharactersDaoFacade {
    private fun resultRowToCharacter(row: ResultRow): Character = Character(
        id = row[Characters.id],
        name = row[Characters.name]
    )


    override suspend fun getCharacter(characterName: String): Character? {
        return dbQuery{
            Characters.select{ Characters.name eq characterName}.map(::resultRowToCharacter).singleOrNull()
        }
    }

    override suspend fun addCharacter(characterName: String): Character? {
        return dbQuery {
            val insertStatement = Characters.insert {
                it[Characters.name] = characterName
            }
            insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToCharacter)
        }
    }
}

val charactersDAO = CharactersDaoFacadeImpl().apply {
    runBlocking {
        addCharacter("The Mandalorian")
        addCharacter("Grogu")
        addCharacter( "The Amorer")
        addCharacter( "Captain Bombardier")
        addCharacter( "Cad Bane")
        addCharacter( "Cara Dune")
        addCharacter( "Boba Fett")
        addCharacter( "Frog Lady")
        addCharacter( "Bib Fortuna")
        addCharacter( "Garsa Fwip")
        addCharacter( "Moff Gideon")
        addCharacter("IG-11")
        addCharacter( "Greef Karga")
        addCharacter("Bo-Katan Kryze")
    }
}