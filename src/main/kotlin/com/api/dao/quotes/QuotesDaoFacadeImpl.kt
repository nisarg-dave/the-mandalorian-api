package com.api.dao.quotes

import com.api.dao.DatabaseFactory.dbQuery
import com.api.models.Quote
import com.api.models.Quotes
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.*
import kotlin.random.Random

class QuotesDaoFacadeImpl : QuotesDaoFacade {
    private fun resultRowToQuote(row: ResultRow):Quote = Quote(
        id = row[Quotes.id],
        quote = row[Quotes.quote],
        season = row[Quotes.season],
        chapter = row[Quotes.chapter],
        character = row[Quotes.character]
    )

    override suspend fun randomQuote(): Quote? {
        return dbQuery{
            val randomNumber = Random.nextInt(1,27)
            Quotes.select { Quotes.id eq randomNumber }.map(::resultRowToQuote).singleOrNull()
        }

    }

    override suspend fun addQuote(quote: String, season: Int, chapter: String, character: String): Quote? {
        return dbQuery {  val insertStatement = Quotes.insert {
                it[Quotes.quote] = quote
                it[Quotes.season] = season
                it[Quotes.chapter] = chapter
                it[Quotes.character] = character
            }
            insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToQuote)
        }
    }

    override suspend fun quotesByCharacter(character: String): List<Quote> {
        return dbQuery {
            Quotes.select {Quotes.character eq character }.map(::resultRowToQuote)
        }
    }

    override suspend fun quotesFromSeason(season: Int): List<Quote> {
        return dbQuery {
            Quotes.select {Quotes.season eq season }.map(::resultRowToQuote)
        }
    }
}

val quotesDAO = QuotesDaoFacadeImpl().apply {
    runBlocking {
        addQuote( "I can bring you in warm or I can bring you in cold.", 1, "Chapter 1", "The Mandalorian")
        addQuote("This is the way.", 1, "Chapter 3", "The Armorer")
        addQuote( "I like those odds.", 1, "Chapter 1", "The Mandalorian")
        addQuote( "I have spoken.", 1, "Chapter 1", "Kuiil")
        addQuote( "Bounty hunting is a complicated profession.", 1, "Chapter 1", "The Client")
        addQuote("I will initiate self-destruct", 1, "Chapter 1", "IG-11")
        addQuote("I’m a Mandalorian. Weapons are part of my religion.", 1, "Chapter 2", "The Mandalorian")
        addQuote( "They all hate you, Mando. Because you're a legend!", 1, "Chapter 3", "Greef Karga")
        addQuote( "When one chooses to walk the Way of the Mandalore, you are both hunter and prey.", 1, "Chapter 3", "The Armorer")
        addQuote( "Stop touching things.", 1, "Chapter 4", "The Mandalorian")
        addQuote( "Bad news. You can’t live here anymore.", 1, "Chapter 4", "The Mandalorian")
        addQuote( "Your name will be legendary", 1, "Chapter 5", "Fennec Shand")
        addQuote( "You are a clan of two.", 1, "Chapter 8", "The Armorer")
        addQuote( "Come on, baby! Do the magic hand thing.", 1, "Chapter 8", "Greef Karga")
        addQuote( "Where I go, he goes.", 2, "Chapter 9", "The Mandalorian")
        addQuote("I guess every once in a while both suns shine on a womp rat’s tail.", 2, "Chapter 9", "Cobb Vanth")
        addQuote( "I’m sorry, lady. I don’t understand frog.", 2, "Chapter 10", "The Mandalorian")
        addQuote("Mandalorians are stronger together.", 2, "Chapter 11", "Bo-Katan Kryze")
        addQuote( "There you will find Ahsoka Tano. Tell her you were sent by Bo-Katan.", 2, "Chapter 12", "Bo-Katan Kyrze")
        addQuote( "Dank farrik.", 2, "Chapter 12", "Cara Dune")
        addQuote( "Ahsoka Tano! Bo-Katan sent me. We need to talk.", 2, "Chapter 13", "The Mandalorian")
        addQuote( "Grogu and I can feel each other’s thoughts.", 2, "Chapter 13", "Ahsoka Tano")
        addQuote( "I’ve seen what such feelings can do to a fully trained Jedi Knight. To the best of us.", 2, "Chapter 13", "Ahsoka Tano")
        addQuote( "I don’t want your armor. I want my armor.", 2, "Chapter 14", "Boba Fett")
        addQuote( "A friendly piece of advice, assume that I know everything.", 2, "Chapter 16", "Moff Gideon")
        addQuote( "Come, little one.", 2, "Chapter 16", "Luke Skywalker")
        addQuote( "I’ll see you again. I promise.", 2, "Chapter 16", "The Mandalorian")
    }
}
