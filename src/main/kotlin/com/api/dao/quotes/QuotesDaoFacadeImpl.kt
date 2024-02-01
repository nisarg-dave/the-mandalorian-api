package com.api.dao.quotes

import com.api.dao.DatabaseFactory.dbQuery
import com.api.models.Quote
import com.api.models.Quotes
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlin.random.Random

class QuotesDaoFacadeImpl : QuotesDaoFacade {
//    Builds a Quote from the row
    private fun resultRowToQuote(row: ResultRow):Quote = Quote(
        id = row[Quotes.id],
        quote = row[Quotes.quote],
        season = row[Quotes.season],
        episode = row[Quotes.episode],
        show = row[Quotes.show],
        character = row[Quotes.character]
    )

    override suspend fun randomQuote(): Quote? {
        return dbQuery{
            val randomNumber = Random.nextInt(1,27)
//            Select function takes a lambda which is of type SqlExpressionBuilder. It defines useful operations on column such as eq, less, plus, times, inList etc.
//            select returns a list of Query values which need to be converted to a Quote using resultRowToQuote which is within the current class and therefore doesn't need class definition
//            Singleornull returns the single element
            Quotes.select { Quotes.id eq randomNumber }.map(::resultRowToQuote).singleOrNull()
        }

    }

    override suspend fun quotesByCharacter(character: String): List<Quote> {
//        equals is an infix notation
        return dbQuery {
            Quotes.select {Quotes.character eq character }.map(::resultRowToQuote)
        }
    }

    override suspend fun quotesByShow(show: String, season: Int?): List<Quote> {
        if(season == null){
            return dbQuery {
                Quotes.select { Quotes.show eq show }.map(::resultRowToQuote)
            }
        }
        return dbQuery {
            Quotes.select {Quotes.show eq show }.map(::resultRowToQuote).filter { it.season == season }
        }
    }

    override suspend fun addQuote(newQuote: Quote): Quote? {
//        Inside the lambda, we are specifying which value is supposed to be set for which column
//        I think because POST returns what you posted, we return it
        return dbQuery {
            val insertStatement = Quotes.insert {
                it[this.show] = newQuote.show
                it[this.season] = newQuote.season
                it[this.episode] = newQuote.episode
                it[this.character] = newQuote.character
                it[this.quote] = newQuote.quote
            }
            insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToQuote)
        }
    }

//    deleteWhere will return how rows it has deleted, and then we check if it is greater than 0
    override suspend fun removeQuote( id: Int ): Boolean = dbQuery { Quotes.deleteWhere { Quotes.id eq id } > 0 }


    override suspend fun editQuote(editedQuote: Quote): Boolean =  dbQuery { Quotes.update({Quotes.id eq editedQuote.id}) {
        it[this.show] = editedQuote.show
        it[this.season] = editedQuote.season
        it[this.episode] = editedQuote.episode
        it[this.character] = editedQuote.character
        it[this.quote] = editedQuote.quote
    } > 0 }

}

val quotesDAO = QuotesDaoFacadeImpl()

//Initializing the Quotes Facade
//val quotesDAO = QuotesDaoFacadeImpl().apply {
//    runBlocking {
//        addQuote( "I can bring you in warm or I can bring you in cold.", 1, "Chapter 1", "The Mandalorian")
//        addQuote("This is the way.", 1, "Chapter 3", "The Armorer")
//        addQuote( "I like those odds.", 1, "Chapter 1", "The Mandalorian")
//        addQuote( "I have spoken.", 1, "Chapter 1", "Kuiil")
//        addQuote( "Bounty hunting is a complicated profession.", 1, "Chapter 1", "The Client")
//        addQuote("I will initiate self-destruct", 1, "Chapter 1", "IG-11")
//        addQuote("I’m a Mandalorian. Weapons are part of my religion.", 1, "Chapter 2", "The Mandalorian")
//        addQuote( "They all hate you, Mando. Because you're a legend!", 1, "Chapter 3", "Greef Karga")
//        addQuote( "When one chooses to walk the Way of the Mandalore, you are both hunter and prey.", 1, "Chapter 3", "The Armorer")
//        addQuote( "Stop touching things.", 1, "Chapter 4", "The Mandalorian")
//        addQuote( "Bad news. You can’t live here anymore.", 1, "Chapter 4", "The Mandalorian")
//        addQuote( "Your name will be legendary", 1, "Chapter 5", "Fennec Shand")
//        addQuote( "You are a clan of two.", 1, "Chapter 8", "The Armorer")
//        addQuote( "Come on, baby! Do the magic hand thing.", 1, "Chapter 8", "Greef Karga")
//        addQuote( "Where I go, he goes.", 2, "Chapter 9", "The Mandalorian")
//        addQuote("I guess every once in a while both suns shine on a womp rat’s tail.", 2, "Chapter 9", "Cobb Vanth")
//        addQuote( "I’m sorry, lady. I don’t understand frog.", 2, "Chapter 10", "The Mandalorian")
//        addQuote("Mandalorians are stronger together.", 2, "Chapter 11", "Bo-Katan Kryze")
//        addQuote( "There you will find Ahsoka Tano. Tell her you were sent by Bo-Katan.", 2, "Chapter 12", "Bo-Katan Kyrze")
//        addQuote( "Dank farrik.", 2, "Chapter 12", "Cara Dune")
//        addQuote( "Ahsoka Tano! Bo-Katan sent me. We need to talk.", 2, "Chapter 13", "The Mandalorian")
//        addQuote( "Grogu and I can feel each other’s thoughts.", 2, "Chapter 13", "Ahsoka Tano")
//        addQuote( "I’ve seen what such feelings can do to a fully trained Jedi Knight. To the best of us.", 2, "Chapter 13", "Ahsoka Tano")
//        addQuote( "I don’t want your armor. I want my armor.", 2, "Chapter 14", "Boba Fett")
//        addQuote( "A friendly piece of advice, assume that I know everything.", 2, "Chapter 16", "Moff Gideon")
//        addQuote( "Come, little one.", 2, "Chapter 16", "Luke Skywalker")
//        addQuote( "I’ll see you again. I promise.", 2, "Chapter 16", "The Mandalorian")
//    }
//}
