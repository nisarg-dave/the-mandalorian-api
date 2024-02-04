package com.api.dao.quotes

import com.api.dao.DatabaseFactory.dbQuery
import com.api.models.Characters
import com.api.models.Quote
import com.api.models.Quotes
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.random.Random

class QuotesDaoFacadeImpl : QuotesDaoFacade {
//    Builds a Quote from the row
    private fun resultRowToQuote(row: ResultRow):Quote = Quote(
        id = row[Quotes.id],
        quote = row[Quotes.quote],
        season = row[Quotes.season],
        episode = row[Quotes.episode],
        show = row[Quotes.show],
        character = row[Characters.name]
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
            (Quotes innerJoin Characters).select {
                Characters.name eq character
            }.map(::resultRowToQuote)
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

    override suspend fun addQuote(show: String, season: Int, episode: String, character: String, quote: String): Quote? {
//        Inside the lambda, we are specifying which value is supposed to be set for which column
//        I think because POST returns what you posted, we return it
        return dbQuery {
            val characterId = Characters.select {Characters.name eq character}.singleOrNull()?.get(Characters.id)

            if(characterId != null){
                val insertStatement = Quotes.insert {
                    it[this.show] = show
                    it[this.season] = season
                    it[this.episode] = episode
                    it[this.characterId] = characterId
                    it[this.quote] = quote
                }
//            Remember with member reference operator it is always in () and not lambda {}
                insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToQuote)
            }
            else {
//              the last expression in a block or function body is automatically treated as the return value
                null
            }

        }
    }

//    deleteWhere will return how rows it has deleted, and then we check if it is greater than 0
    override suspend fun removeQuote( id: Int ): Boolean = dbQuery { Quotes.deleteWhere { Quotes.id eq id } > 0 }


    override suspend fun editQuote(id:Int, show: String, season: Int, episode: String, character: String, quote: String): Boolean {
        return dbQuery {
            val characterId = Characters.select {Characters.name eq character}.singleOrNull()?.get(Characters.id)
            if(characterId != null){
                Quotes.update({Quotes.id eq id}) {
                    it[this.show] = show
                    it[this.season] = season
                    it[this.episode] = episode
                    it[this.characterId] = characterId
                    it[this.quote] = quote
                } > 0
            }
            else {
                false
            }
        }
    }
}

//Initializing the Quotes Facade
val quotesDAO = QuotesDaoFacadeImpl().apply {
    runBlocking {
//        Have to add each field individually instead of passing Quote object because otherwise ID is required
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "The Mandalorian", quote = "I can bring you in warm or I can bring you in cold.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "The Mandalorian", quote = "I like those odds.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "Kuiil", quote = "I have spoken.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "The Client", quote = "Bounty hunting is a complicated profession.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "IG-11", quote = "I will initiate self-destruct.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 2", character = "The Mandalorian", quote = "I’m a Mandalorian. Weapons are part of my religion.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 3", character = "Greef Karga", quote = "They all hate you, Mando. Because you're a legend!")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 3", character = "The Armorer", quote = "When one chooses to walk the Way of the Mandalore, you are both hunter and prey.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 3", character = "The Armorer", quote = "This is the way.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 4", character = "The Mandalorian", quote = "Stop touching things.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 4", character = "The Mandalorian", quote = "Bad news. You can’t live here anymore.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 5", character = "Fennec Shand", quote = "Your name will be legendary.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "The Armorer", quote = "You are a clan of two.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "Greef Karga", quote = "Come on, baby! Do the magic hand thing.")
        addQuote(show = "The Mandalorian", season = 2, episode = "Chapter 9", character = "The Mandalorian", quote = "Where I go, he goes.")
        addQuote(show = "The Mandalorian", season = 2, episode = "Chapter 9", character = "Cobb Vanth", quote = "I guess every once in a while both suns shine on a womp rat’s tail.")
        addQuote(show = "The Mandalorian", season = 2, episode = "Chapter 10", character = "The Mandalorian", quote = "I’m sorry, lady. I don’t understand frog.")
        addQuote(show = "The Mandalorian", season = 2, episode = "Chapter 11", character = "Bo-Katan Kryze", quote = "Mandalorians are stronger together.")
        addQuote(show = "The Mandalorian", season = 2, episode = "Chapter 12", character = "Bo-Katan Kryze", quote = "There you will find Ahsoka Tano. Tell her you were sent by Bo-Katan.")
        addQuote(show = "The Mandalorian", season = 2, episode = "Chapter 12", character = "Cara Dune", quote = "Dank farrik.")
        addQuote(show = "The Mandalorian", season = 2, episode = "Chapter 13", character = "The Mandalorian", quote = "Ahsoka Tano! Bo-Katan sent me. We need to talk.")
        addQuote(show = "The Mandalorian", season = 2, episode = "Chapter 13", character = "Ahsoka Tano", quote = "Grogu and I can feel each other’s thoughts.")
        addQuote(show = "The Mandalorian", season = 2, episode = "Chapter 13", character = "Ahsoka Tano", quote = "I’ve seen what such feelings can do to a fully trained Jedi Knight. To the best of us.")
        addQuote(show = "The Mandalorian", season = 2, episode = "Chapter 14", character = "Boba Fett", quote = "I don’t want your armor. I want my armor.")
        addQuote(show = "The Mandalorian", season = 2, episode = "Chapter 16", character = "Moff Gideon", quote = "A friendly piece of advice, assume that I know everything.")
        addQuote(show = "The Mandalorian", season = 2, episode = "Chapter 16", character = "Luke Skywalker", quote = "Come, little one.")
        addQuote(show = "The Mandalorian", season = 2, episode = "Chapter 16", character = "The Mandalorian", quote = "I’ll see you again. I promise.")
        addQuote(show = "The Book of Boba Fett", season = 1, episode = "Chapter 1", character = "Boba Fett", quote = "I’m the crime lord. He’s supposed to pay me.")
        addQuote(show = "The Book of Boba Fett", season = 1, episode = "Chapter 1", character = "Fennec Shand", quote = "Shall I kill him?")
        addQuote(show = "The Book of Boba Fett", season = 1, episode = "Chapter 1", character = "Boba Fett", quote = "Jabba ruled with fear. I intend to rule with respect.")
        addQuote(show = "The Book of Boba Fett", season = 1, episode = "Chapter 2", character = "Hutt Twin", quote = "Sleep lightly, bounty hunter.")
        addQuote(show = "The Book of Boba Fett", season = 1, episode = "Chapter 2", character = "Boba Fett", quote = "Like a bantha.")
        addQuote(show = "The Book of Boba Fett", season = 1, episode = "Chapter 3", character = "Fennec Shand", quote = "If you wish to continue breathing, I advise you to weigh your next words carefully.")
        addQuote(show = "The Book of Boba Fett", season = 1, episode = "Chapter 3", character = "Boba Fett", quote = "No hard feelings. It’s just business.")
        addQuote(show = "The Book of Boba Fett", season = 1, episode = "Chapter 4", character = "Boba Fett", quote = "Find other banthas. Make baby banthas. Go!")
        addQuote(show = "The Book of Boba Fett", season = 1, episode = "Chapter 4", character = "Boba Fett", quote = "Do you know who I am? I am Boba Fett.")
        addQuote(show = "The Book of Boba Fett", season = 1, episode = "Chapter 4", character = "Boba Fett", quote = "You can only get so far without a tribe.")
        addQuote(show = "The Book of Boba Fett", season = 1, episode = "Chapter 4", character = "Garsa Fwip", quote = "Hit it, Max.")
        addQuote(show = "The Book of Boba Fett", season = 1, episode = "Chapter 5", character = "The Mandalorian", quote = "Loyalty and solidarity are the way.")
        addQuote(show = "The Book of Boba Fett", season = 1, episode = "Chapter 5", character = "Peli Motto", quote = "Dated a Jawa for a while. They’re quite furry. Very furry.")
        addQuote(show = "The Book of Boba Fett", season = 1, episode = "Chapter 6", character = "Luke Skywalker", quote = "Get back up. Always get back up.")
        addQuote(show = "The Book of Boba Fett", season = 1, episode = "Chapter 6", character = "Ahsoka Tano", quote = "So much like your father.")
        addQuote(show = "The Book of Boba Fett", season = 1, episode = "Chapter 6", character = "Cad Bane", quote = "I’d be careful where I was sticking my nose if I were you.")
        addQuote(show = "The Book of Boba Fett", season = 1, episode = "Chapter 7", character = "The Mandalorian", quote = "We’ll both die in the name of honor.")
        addQuote(show = "The Book of Boba Fett", season = 1, episode = "Chapter 7", character = "Boba Fett", quote = "This is my city!")
        addQuote(show = "The Book of Boba Fett", season = 1, episode = "Chapter 7", character = "Fennec Shand", quote = "If not us, then who?")
        addQuote(show = "Ahsoka", season = 1, episode = "Part One", character = "Ahsoka Tano", quote = "Let's just say I didn't follow standard Jedi protocol.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part One", character = "Ahsoka Tano", quote = "Sometimes the right reasons have the wrong consequences.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part One", character = "Ahsoka Tano", quote = "This isn't just about finding Ezra. It’s about preventing another war.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part One", character = "Ezra Bridger", quote = "I’m counting on you to see this through.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Two", character = "Hera Syndulla", quote = "You both need to help each other.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Two", character = "Baylan Skoll", quote = "You speak of dreams.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Two", character = "Morgan Elsbeth", quote = "Threads of fate do not lie.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Two", character = "Huyang", quote = "Your aptitude for the force falls short of them.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Three", character = "Ahsoka Tano", quote = "Learning to wield the force takes a deeper commitment.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Three", character = "Ahsoka Tano", quote = "I don’t need Sabine to be a Jedi, I need her to be herself.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Three", character = "Hera Syndulla", quote = "Were you ever in the war senator?")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Five", character = "Anakin Skywalker", quote = "Live or die?")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Five", character = "Anakin Skywalker", quote = "One is never too old to learn, Snips.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Five", character = "Ahsoka Tano", quote = "I choose to live.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Six", character = "Great Mothers", quote = "Welcome Child of Dathomir, you do our ancestors great credit.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Six", character = "Morgan Elsbeth", quote = "Your vision guided me across the stars.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Six", character = "Baylan Skoll", quote = "The fall of the Jedi, rise of the Empire. It repeats again and again and again.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Six", character = "Thrawn", quote = "What was first just a dream has become a frightening reality for those who may oppose us.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Six", character = "Thrawn", quote = "The desire to be reunited with a long lost friend.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Six", character = "Thrawn", quote = "You have gambled the fate of your galaxy in that belief.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Six", character = "Enoch", quote = "Be warned, nomads wonder this wasted land and prey on each other for survival.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Six", character = "Baylan Skoll", quote = "Comes from a breed of Bokken Jedi, trained in the wild after the Temple fell.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Seven", character = "Hera Syndulla", quote = "We have to prepare for the worst and hope for the best.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Seven", character = "Thrawn", quote = "We will always be one step ahead of her.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Seven", character = "Baylan Skoll", quote = "Your ambition drives you in one direction, my path lies in another.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Seven", character = "Baylan Skoll", quote = "Impatience for victory will guarantee defeat.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Eight", character = "Great Mothers", quote = "You shall be rewarded, the gift of shadows.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Eight", character = "Great Mothers", quote = "The Blade of Talzin.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Eight", character = "Huyang", quote = "Old enough to know that the relationship between a master and an apprentice is as challenging as it is meaningful.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Eight", character = "Ahsoka Tano", quote = "Over the years I’ve made my share of difficult choices.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Eight", character = "Ahsoka Tano", quote = "He always stood by me.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Eight", character = "Ahsoka Tano", quote = "Train your mind. Train your body. Trust in the Force.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Eight", character = "Thrawn", quote = "We cannot underestimate the apprentice of Anakin Skywalker.")
        addQuote(show = "Ahsoka", season = 1, episode = "Part Eight", character = "Thrawn", quote = "One wonders how similar you might become.")
    }
}
