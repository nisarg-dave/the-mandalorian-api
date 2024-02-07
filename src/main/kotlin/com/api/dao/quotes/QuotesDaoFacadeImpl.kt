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
    private fun resultRowToQuote(row: ResultRow):Quote {
        val characterName = Characters.select { Characters.id eq row[Quotes.characterId] }.singleOrNull()?.get(Characters.name)
        return Quote(
        id = row[Quotes.id],
        quote = row[Quotes.quote],
        season = row[Quotes.season],
        episode = row[Quotes.episode],
        show = row[Quotes.show],
        character = characterName ?: "Unknown character"
    )}

    override suspend fun randomQuote(): Quote? {
        return dbQuery{
            val randomNumber = Random.nextInt(1,27)
//            Select function takes a lambda which is of type SqlExpressionBuilder. It defines useful operations on column such as eq, less, plus, times, inList etc.
//            select returns a list of Query values which need to be converted to a Quote using resultRowToQuote which is within the current class and therefore doesn't need class definition
//            Singleornull returns the single element
//            Query inherits Iterable, so it is possible to traverse it
            Quotes.select { Quotes.id eq randomNumber }.map(::resultRowToQuote).singleOrNull()
        }

    }

    override suspend fun quotesByCharacter(character: String): List<Quote> {
//        equals is an infix notation
        return dbQuery {
//            When joining on a foreign key, join function becomes more concise to innerJoin
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
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "Kuiil", quote = "You are a Mandalorian! Your ancestors rode the great Mythosaur. Surely you can ride this young foal.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "Kuiil", quote = "They do not belong here. Those that live here come to seek peace. There will be no peace until they're gone.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "The Client", quote = "Bounty hunting is a complicated profession.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "The Client", quote = "Greef Karga said you were coming.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "The Client", quote = "He said you were the best in the parsec. He also said you were expensive. Very expensive.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "Dr. Pershing", quote = "That is not what we agreed upon.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "IG-11", quote = "I will initiate self-destruct.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "IG-11", quote = "You are a Guild member? I thought I was the only one on assignment.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "IG-11", quote = "I will, of course, receive the reputation merits associated with the mission.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 2", character = "The Mandalorian", quote = "I’m a Mandalorian. Weapons are part of my religion.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 2", character = "The Mandalorian", quote = "I'm not gonna trade anything. These are my parts. They stole from me.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 2", character = "Kuiil", quote = "Thank you for bringing peace to my valley. And good luck with the child. May it survive and bring you a handsome reward.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 3", character = "The Client", quote = "How uncharacteristic of one of your reputation. You have taken both commission and payment. Is it not the code of the Guild that these events are now forgotten? That Beskar is enough to make a handsome replacement for your armor. Unfortunately, finding a Mandalorian in these trying times is more difficult than finding the steel.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 3", character = "Greef Karga", quote = "They all hate you, Mando. Because you're a legend!")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 3", character = "Dr Pershing", quote = "I-I protected him. I protected him. If it wasn't for me, he would already be dead! Please! Please. Please.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 3", character = "Paz Vizla", quote = "Get out of here! We'll hold 'em off!")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 3", character = "The Mandalorian", quote = "You're going to have to relocate the covert.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 3", character = "The Armorer", quote = "When one chooses to walk the Way of the Mandalore, you are both hunter and prey.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 3", character = "The Armorer", quote = "This is the way.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 4", character = "The Mandalorian", quote = "Stop touching things.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 4", character = "Cara Dune", quote = "It's gonna break his little heart.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 4", character = "The Mandalorian", quote = "Bad news. You can’t live here anymore.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 4", character = "Cara Dune", quote = "You cannot fight that thing.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 4", character = "Cara Dune", quote = "Well, let's just call it an early retirement. Look, I knew you were Guild. I figured you had a fob on me. That's why I came at you so hard.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 4", character = "Cara Dune", quote = "So, we're basically running off a band of raiders for lunch money?")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 5", character = "Fennec Shand", quote = "Look, there's still time to make my rendezvous in Mos Espa. Take me to it and I can pay you double the price on my head.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 5", character = "Toro Calican", quote = "I don't care about the money.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 5", character = "Toro Calican", quote = "Bringin' you in will make me a full member of the Bounty Hunters' Guild.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 5", character = "Fennec Shand", quote = "The Mandalorian. His armor alone is worth more than my bounty.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 5", character = "Fennec Shand", quote = "Like I said, you don't see many. You bring the Guild that traitor, and they'll welcome you with open arms. Your name will be legendary.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 5", character = "Toro Calican", quote = "Picked up this bounty puck before I left the Mid-Rim. Fennec Shand, an assassin. Heard she's been on the run ever since the New Republic put all her employers in lockdown.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 5", character = "The Mandalorian", quote = "She's got the high ground. She'll wait for us to make the first move. I'm gonna rest. You take the first watch. Stay low!")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 5", character = "Peli Motto", quote = "Now, here's the plan. I am going to look after you until The Mandalorian gets back, and then I'm gonna charge him extra for watching you.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 5", character = "Peli Motto", quote = "You damage one of my droids, you'll pay for it.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 5", character = "Peli Motto", quote = "It's okay. You woke it up. Do you have any idea how long it took me to get it to sleep?")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 6", character = "Ranzar Malk", quote = "Yeah, one of our associates ran afoul of some competitors and got himself caught. So I'm puttin' together a crew to spring him. It's a five-person job. I got four. All I need is the ride, and you brought it.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 6", character = "Xi'an", quote = "Tell me why I shouldn't cut you down where you stand?")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 6", character = "Mayfeld", quote = "You better be good for it.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 6", character = "The Mandalorian", quote = "We're not killing anybody, you understand?")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 6", character = "Mayfeld", quote = "Get that blaster out of my face, Mando.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 6", character = "Mayfeld", quote = "If he presses that thing, we're all done. A New Republic attack team will hone in on that signal and blow us all to hell. Put it down!")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 6", character = "Mayfeld", quote = "Me, I was never into pets. Yeah, I didn't have the temperament. Patience, you know? I mean, I tried, but never worked out. But I'm thinkin' maybe I'll try again.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 7", character = "The Mandalorian", quote = "I've run into some problems.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 7", character = "Kuiil", quote = "I figured as much. Why else would you return?")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 7", character = "Kuiil", quote = "I'm not suited for such work. I can re-program IG-11 for nursing and protocol.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 7", character = "Greef Karga", quote = "There's something you should know. The plan was to kill you and take the kid. But after what happened last night, I couldn't go through with it. Go on. You can gun me down here and now and it wouldn't violate the Code. But if you do, this child will never be safe.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 7", character = "Cara Dune", quote = "So, we're going to Nevarro?")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 7", character = "The Mandalorian", quote = "Hard to tell. No insignia anymore. I took out the safehouse when I snatched the kid. More Imps have reinforced since.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 7", character = "Cara Dune", quote = "The Ugnaught said he re-wired it.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 7", character = "Moff Gideon", quote = "Have they brought the child?")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "Cara Dune", quote = "Is there another way out?")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "The Mandalorian", quote = "The Mandalorians have a covert down in the sewers. If we can get down there, they can help us escape.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "The Mandalorian", quote = "I was a foundling. They raised me in the Fighting Corps. I was treated as one of their own. When I came of age, I was sworn to the Creed. The only record of my family name was in the registers of Mandalore. Moff Gideon was an ISB officer during the purge. That's how I know it's him.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "The Armorer", quote = "You are a clan of two.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "The Armorer", quote = "It was not his fault. We revealed ourselves. We knew what could happen if we left the covert. The Imperials arrived shortly thereafter. This is what resulted.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "The Armorer", quote = "Some may have escaped off-world.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "IG-11", quote = "They will not be satisfied with anything less than the child. This is unacceptable. I will eliminate the enemy and you will escape.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "Greef Karga", quote = "Come on, baby! Do the magic hand thing.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "The Mandalorian", quote = "You protect the child. I can hold them back long enough for you to escape. Let me have a warrior's death.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "Cara Dune", quote = "I won't leave you.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "Moff Gideon", quote = "If you're asking if you can trust me, you cannot. Just as you betrayed our business arrangement, I would gladly break any promise and watch you die at my hand. The assurance I give is this: I will act in my own self-interest, which at this time involves your cooperation and benefit.")
        addQuote(show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "Moff Gideon", quote = "Your astute panic suggests that you understand your situation. I would prefer to avoid any further violence, and encourage a moment of consideration.")




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
