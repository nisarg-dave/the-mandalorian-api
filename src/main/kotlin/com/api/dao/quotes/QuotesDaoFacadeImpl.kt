package com.api.dao.quotes

import com.api.dao.DatabaseFactory.dbQuery
import com.api.models.*
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
fun insertQuotes(){
    val quotes = listOf(
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "The Mandalorian", quote = "I can bring you in warm or I can bring you in cold."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "The Mandalorian", quote = "I like those odds."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "Kuiil", quote = "I have spoken."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "Kuiil", quote = "You are a Mandalorian! Your ancestors rode the great Mythosaur. Surely you can ride this young foal."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "Kuiil", quote = "They do not belong here. Those that live here come to seek peace. There will be no peace until they're gone."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "The Client", quote = "Bounty hunting is a complicated profession."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "The Client", quote = "Greef Karga said you were coming."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "The Client", quote = "He said you were the best in the parsec. He also said you were expensive. Very expensive."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "Dr. Pershing", quote = "That is not what we agreed upon."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "IG-11", quote = "I will initiate self-destruct."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "IG-11", quote = "You are a Guild member? I thought I was the only one on assignment."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "IG-11", quote = "I will, of course, receive the reputation merits associated with the mission."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 2", character = "The Mandalorian", quote = "I’m a Mandalorian. Weapons are part of my religion."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 2", character = "The Mandalorian", quote = "I'm not gonna trade anything. These are my parts. They stole from me."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 2", character = "Kuiil", quote = "Thank you for bringing peace to my valley. And good luck with the child. May it survive and bring you a handsome reward."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 3", character = "The Client", quote = "How uncharacteristic of one of your reputation. You have taken both commission and payment. Is it not the code of the Guild that these events are now forgotten? That Beskar is enough to make a handsome replacement for your armor. Unfortunately, finding a Mandalorian in these trying times is more difficult than finding the steel."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 3", character = "Greef Karga", quote = "They all hate you, Mando. Because you're a legend!"),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 3", character = "Dr. Pershing", quote = "I-I protected him. I protected him. If it wasn't for me, he would already be dead! Please! Please. Please."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 3", character = "Paz Vizla", quote = "Get out of here! We'll hold 'em off!"),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 3", character = "The Mandalorian", quote = "You're going to have to relocate the covert."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 3", character = "The Armorer", quote = "When one chooses to walk the Way of the Mandalore, you are both hunter and prey."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 3", character = "The Armorer", quote = "This is the way."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 4", character = "The Mandalorian", quote = "Stop touching things."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 4", character = "Cara Dune", quote = "It's gonna break his little heart."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 4", character = "The Mandalorian", quote = "Bad news. You can’t live here anymore."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 4", character = "Cara Dune", quote = "You cannot fight that thing."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 4", character = "Cara Dune", quote = "Well, let's just call it an early retirement. Look, I knew you were Guild. I figured you had a fob on me. That's why I came at you so hard."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 4", character = "Cara Dune", quote = "So, we're basically running off a band of raiders for lunch money?"),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 5", character = "Fennec Shand", quote = "Look, there's still time to make my rendezvous in Mos Espa. Take me to it and I can pay you double the price on my head."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 5", character = "Toro Calican", quote = "I don't care about the money."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 5", character = "Toro Calican", quote = "Bringin' you in will make me a full member of the Bounty Hunters' Guild."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 5", character = "Fennec Shand", quote = "The Mandalorian. His armor alone is worth more than my bounty."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 5", character = "Fennec Shand", quote = "Like I said, you don't see many. You bring the Guild that traitor, and they'll welcome you with open arms. Your name will be legendary."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 5", character = "Toro Calican", quote = "Picked up this bounty puck before I left the Mid-Rim. Fennec Shand, an assassin. Heard she's been on the run ever since the New Republic put all her employers in lockdown."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 5", character = "The Mandalorian", quote = "She's got the high ground. She'll wait for us to make the first move. I'm gonna rest. You take the first watch. Stay low!"),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 5", character = "Peli Motto", quote = "Now, here's the plan. I am going to look after you until The Mandalorian gets back, and then I'm gonna charge him extra for watching you."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 5", character = "Peli Motto", quote = "You damage one of my droids, you'll pay for it."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 5", character = "Peli Motto", quote = "It's okay. You woke it up. Do you have any idea how long it took me to get it to sleep?"),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 6", character = "Ranzar Malk", quote = "Yeah, one of our associates ran afoul of some competitors and got himself caught. So I'm puttin' together a crew to spring him. It's a five-person job. I got four. All I need is the ride, and you brought it."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 6", character = "Xi'an", quote = "Tell me why I shouldn't cut you down where you stand?"),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 6", character = "Mayfeld", quote = "You better be good for it."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 6", character = "The Mandalorian", quote = "We're not killing anybody, you understand?"),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 6", character = "Mayfeld", quote = "Get that blaster out of my face, Mando."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 6", character = "Mayfeld", quote = "If he presses that thing, we're all done. A New Republic attack team will hone in on that signal and blow us all to hell. Put it down!"),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 6", character = "Mayfeld", quote = "Me, I was never into pets. Yeah, I didn't have the temperament. Patience, you know? I mean, I tried, but never worked out. But I'm thinkin' maybe I'll try again."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 7", character = "The Mandalorian", quote = "I've run into some problems."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 7", character = "Kuiil", quote = "I figured as much. Why else would you return?"),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 7", character = "Kuiil", quote = "I'm not suited for such work. I can re-program IG-11 for nursing and protocol."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 7", character = "Greef Karga", quote = "There's something you should know. The plan was to kill you and take the kid. But after what happened last night, I couldn't go through with it. Go on. You can gun me down here and now and it wouldn't violate the Code. But if you do, this child will never be safe."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 7", character = "Cara Dune", quote = "So, we're going to Nevarro?"),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 7", character = "The Mandalorian", quote = "Hard to tell. No insignia anymore. I took out the safehouse when I snatched the kid. More Imps have reinforced since."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 7", character = "Cara Dune", quote = "The Ugnaught said he re-wired it."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 7", character = "Moff Gideon", quote = "Have they brought the child?"),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "Cara Dune", quote = "Is there another way out?"),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "The Mandalorian", quote = "The Mandalorians have a covert down in the sewers. If we can get down there, they can help us escape."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "The Mandalorian", quote = "I was a foundling. They raised me in the Fighting Corps. I was treated as one of their own. When I came of age, I was sworn to the Creed. The only record of my family name was in the registers of Mandalore. Moff Gideon was an ISB officer during the purge. That's how I know it's him."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "The Armorer", quote = "You are a clan of two."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "The Armorer", quote = "It was not his fault. We revealed ourselves. We knew what could happen if we left the covert. The Imperials arrived shortly thereafter. This is what resulted."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "The Armorer", quote = "Some may have escaped off-world."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "IG-11", quote = "They will not be satisfied with anything less than the child. This is unacceptable. I will eliminate the enemy and you will escape."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "Greef Karga", quote = "Come on, baby! Do the magic hand thing."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "The Mandalorian", quote = "You protect the child. I can hold them back long enough for you to escape. Let me have a warrior's death."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "Cara Dune", quote = "I won't leave you."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "Moff Gideon", quote = "If you're asking if you can trust me, you cannot. Just as you betrayed our business arrangement, I would gladly break any promise and watch you die at my hand. The assurance I give is this: I will act in my own self-interest, which at this time involves your cooperation and benefit."),
        QuoteContent(show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "Moff Gideon", quote = "Your astute panic suggests that you understand your situation. I would prefer to avoid any further violence, and encourage a moment of consideration."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 9", character = "The Mandalorian", quote = "Where I go, he goes."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 9", character = "Peli Motto", quote = "Care for me to watch this wrinkled critter while you seek out adventure?"),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 9", character = "The Mandalorian", quote = "I've been quested to bring this one back to its kind."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 9", character = "Peli Motto", quote = "Okay. This is a map of Tatooine before the war. You got Mos Eisley, Mos Espa, and up around this region, Mos Pelgo."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 9", character = "Cobb Vanth", quote = "What brings you here, stranger?"),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 9", character = "Cobb Vanth", quote = "They look to me to protect 'em. But a krayt dragon is too much for me to take on alone. Help me kill it, I'll give you the armor."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 9", character = "Cobb Vanth", quote = "These monsters can't be reasoned with."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 9", character = "The Mandalorian", quote = "The same thing I'm telling you. If we fight amongst ourselves, the monster will kill us all."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 9", character = "Cobb Vanth", quote = "I guess every once in a while both suns shine on a womp rat’s tail."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 10", character = "Peli Motto", quote = "She needs her eggs fertilized by the equinox or her line will end. If you jump into hyperspace, they'll die. She said her husband has settled on the estuary moon of Trask in the system of the gas giant Kol Iben."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 10", character = "Peli Motto", quote = "What can I say? I'm an excellent judge of character."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 10", character = "Peli Motto", quote = "All right. He says the contact will rendezvous at the hangar. They'll tell where to find some Mandalorians. That's what you wanted, right?"),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 10", character = "The Mandalorian", quote = "I’m sorry, lady. I don’t understand frog."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 10", character = "Frog Lady", quote = " thought honoring one's word was a part of the Mandalorian code. I guess those are just stories for children."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 10", character = "Carson Teva", quote = "Razor Crest, stand down. We will fire. I repeat, we will fire."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 10", character = "The Mandalorian", quote = "Am I under arrest?"),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 10", character = "Carson Teva", quote = "Technically, you should be. But these are trying times."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 11", character = "The Mandalorian", quote = "Where did you get that armor?"),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 11", character = "Bo-Katan Kryze", quote = "This armor has been in my family for three generations."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 11", character = "Axe Woves", quote = "He's one of them."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 11", character = "Bo-Katan Kryze", quote = "I am Bo-Katan of Clan Kryze. I was born on Mandalore and fought the Purge. I am the last of my line. And you are a Child of the Watch."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 11", character = "Bo-Katan Kryze", quote = "Children of the Watch are a cult of religious zealots that broke away from the Mandalore society. Their goal was to re-establish the ancient way."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 11", character = "The Mandalorian", quote = "There is only one way. The Way of the Mandalore."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 11", character = "Bo-Katan Kryze", quote = "Trask is a black market port. They're staging weapons that have been bought and sold with the plunders of our planet. We're seizing these weapons and using them to retake our home world. Once we've done that, we'll seat a new Mandalore on the throne."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 11", character = "Bo-Katan Kryze", quote = "The Darksaber. Does he have it?"),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 11", character = "Bo-Katan Kryze", quote = "Take the foundling to the city of Calodon on the forest planet of Corvus. There you will find Ahsoka Tano. Tell her you were sent by Bo-Katan. And thank you. Your bravery will not be forgotten. This is the Way."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 11", character = "Bo-Katan Kryze", quote = "Mandalorians are stronger together."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 11", character = "Moff Gideon", quote = "Long live the Empire."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 12", character = "The Mandalorian", quote = "Looks like you two have been busy."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 12", character = "Greef Karga", quote = "I myself have been steeped in clerical work. Marshal Dune here is to be thanked for cleaning up the town."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 12", character = "Cara Dune", quote = "Dank farrik."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 12", character = "Cara Dune", quote = "This is Nevarro. We're here. This entire area's a green zone. Completely safe. But over on this side is the problem."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 12", character = "Cara Dune", quote = "No, this isn't a military operation. This is a lab. We need to get into the system and figure out what's going on."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 12", character = "Cara Dune", quote = "He'll be fine here. You have my word."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 12", character = "Dr. Pershing", quote = "I highly doubt we'll find a donor with a higher M-count, though."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 12", character = "Dr. Pershing", quote = "The child is small, and I was only able to harvest a limited amount without killing him. If these experiments are to continue as requested, we would again require access to the donor. I will not disappoint you again, Moff Gideon."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 12", character = "Carson Teva", quote = "You did a hell of a job cleaning up the system. According to records, you're quite a soldier. We could really use you."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 13", character = "Morgan Elsbeth", quote = "Come forward. You're a Mandalorian?"),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 13", character = "Morgan Elsbeth", quote = "I have a proposition that may interest you."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 13", character = "Morgan Elsbeth", quote = "One that you are well-suited for. The Jedi are the ancient enemy of Mandalore."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 13", character = "Morgan Elsbeth", quote = "Pure beskar, like your armor. Kill the Jedi and it's yours."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 13", character = "The Mandalorian", quote = "Ahsoka Tano! Bo-Katan sent me. We need to talk."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 13", character = "Ahsoka Tano", quote = "I hope it's about him."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 13", character = "Ahsoka Tano", quote = "Grogu and I can feel each other’s thoughts."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 13", character = "Ahsoka Tano", quote = "He was raised at the Jedi Temple on Coruscant. Many Masters trained him over the years. At the end of the Clone Wars when the Empire rose to power, he was hidden. Someone took him from the Temple. Then his memories becomes dark. He seemed lost. Alone. I've known one other being like this. A wise Jedi Master named Yoda."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 13", character = "Ahsoka Tano", quote = "He's formed a strong attachment to you. I cannot train him."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 13", character = "Ahsoka Tano", quote = "I’ve seen what such feelings can do to a fully trained Jedi Knight. To the best of us."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 13", character = "Ahsoka Tano", quote = "The Force is what gives him his powers. It is an energy field created by all living things. To wield it takes a great deal of training and discipline."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 13", character = "The Mandalorian", quote = "I've seen him do things I can't explain. My task was to bring him to a Jedi."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 13", character = "The Mandalorian", quote = "A Mandalorian and a Jedi? They'll never see it coming."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 13", character = "Ahsoka Tano", quote = "You're like a father to him. I cannot train him."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 13", character = "Ahsoka Tano", quote = "Go to the planet Tython. You will find the ancient ruins of a temple that has a strong connection to the Force. Place Grogu on the seeing stone at the top of the mountain."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 13", character = "Ahsoka Tano", quote = "May the Force be with you."),

        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 14", character = "The Mandalorian", quote = "Oh, come on, kid. Ahsoka told me all I had to do was get you here and you'd do the rest."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 14", character = "Boba Fett", quote = "I don’t want your armor. I want my armor that you got from Cobb Vanth back on Tatooine. It belongs to me."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 14", character = "Boba Fett", quote = "I'm a simple man making his way through the galaxy. Like my father before me."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 14", character = "Boba Fett", quote = "Beskar. I want you to take a look at something. My chain code has been encoded in this armor for 25 years. You see, this is me. Boba Fett. This is my father, Jango Fett."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 14", character = "Fennec Shand", quote = "You look like you've just seen a ghost."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 14", character = "Boba Fett", quote = "She was left for dead on the sands of Tatooine, as was I. But fate sometimes steps in to rescue the wretched."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 14", character = "Fennec Shand", quote = "You look like you've just seen a ghost."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 14", character = "Moff Gideon", quote = "You've gotten very good with that. But it makes you oh-so sleepy. Have you ever seen one of these?"),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 14", character = "Moff Gideon", quote = "Oh, uh-uh-uh. You're not ready to play with such things. Liable to put an eye out with one of these. Looks like you could use a nice, long sleep."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 14", character = "Boba Fett", quote = "Until he has returned to you safely, we are in your debt."),


        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 16", character = "Moff Gideon", quote = "A friendly piece of advice, assume that I know everything."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 16", character = "Luke Skywalker", quote = "Come, little one."),
        QuoteContent(show = "The Mandalorian", season = 2, episode = "Chapter 16", character = "The Mandalorian", quote = "I’ll see you again. I promise."),
        QuoteContent(show = "The Book of Boba Fett", season = 1, episode = "Chapter 1", character = "Boba Fett", quote = "I’m the crime lord. He’s supposed to pay me."),
        QuoteContent(show = "The Book of Boba Fett", season = 1, episode = "Chapter 1", character = "Fennec Shand", quote = "Shall I kill him?"),
        QuoteContent(show = "The Book of Boba Fett", season = 1, episode = "Chapter 1", character = "Boba Fett", quote = "Jabba ruled with fear. I intend to rule with respect."),
        QuoteContent(show = "The Book of Boba Fett", season = 1, episode = "Chapter 2", character = "Hutt Twin", quote = "Sleep lightly, bounty hunter."),
        QuoteContent(show = "The Book of Boba Fett", season = 1, episode = "Chapter 2", character = "Boba Fett", quote = "Like a bantha."),
        QuoteContent(show = "The Book of Boba Fett", season = 1, episode = "Chapter 3", character = "Fennec Shand", quote = "If you wish to continue breathing, I advise you to weigh your next words carefully."),
        QuoteContent(show = "The Book of Boba Fett", season = 1, episode = "Chapter 3", character = "Boba Fett", quote = "No hard feelings. It’s just business."),
        QuoteContent(show = "The Book of Boba Fett", season = 1, episode = "Chapter 4", character = "Boba Fett", quote = "Find other banthas. Make baby banthas. Go!"),
        QuoteContent(show = "The Book of Boba Fett", season = 1, episode = "Chapter 4", character = "Boba Fett", quote = "Do you know who I am? I am Boba Fett."),
        QuoteContent(show = "The Book of Boba Fett", season = 1, episode = "Chapter 4", character = "Boba Fett", quote = "You can only get so far without a tribe."),
        QuoteContent(show = "The Book of Boba Fett", season = 1, episode = "Chapter 4", character = "Garsa Fwip", quote = "Hit it, Max."),
        QuoteContent(show = "The Book of Boba Fett", season = 1, episode = "Chapter 5", character = "The Mandalorian", quote = "Loyalty and solidarity are the way."),
        QuoteContent(show = "The Book of Boba Fett", season = 1, episode = "Chapter 5", character = "Peli Motto", quote = "Dated a Jawa for a while. They’re quite furry. Very furry."),
        QuoteContent(show = "The Book of Boba Fett", season = 1, episode = "Chapter 6", character = "Luke Skywalker", quote = "Get back up. Always get back up."),
        QuoteContent(show = "The Book of Boba Fett", season = 1, episode = "Chapter 6", character = "Ahsoka Tano", quote = "So much like your father."),
        QuoteContent(show = "The Book of Boba Fett", season = 1, episode = "Chapter 6", character = "Cad Bane", quote = "I’d be careful where I was sticking my nose if I were you."),
        QuoteContent(show = "The Book of Boba Fett", season = 1, episode = "Chapter 7", character = "The Mandalorian", quote = "We’ll both die in the name of honor."),
        QuoteContent(show = "The Book of Boba Fett", season = 1, episode = "Chapter 7", character = "Boba Fett", quote = "This is my city!"),
        QuoteContent(show = "The Book of Boba Fett", season = 1, episode = "Chapter 7", character = "Fennec Shand", quote = "If not us, then who?"),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part One", character = "Ahsoka Tano", quote = "Let's just say I didn't follow standard Jedi protocol."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part One", character = "Ahsoka Tano", quote = "Sometimes the right reasons have the wrong consequences."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part One", character = "Ahsoka Tano", quote = "This isn't just about finding Ezra. It’s about preventing another war."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part One", character = "Ezra Bridger", quote = "I’m counting on you to see this through."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Two", character = "Hera Syndulla", quote = "You both need to help each other."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Two", character = "Baylan Skoll", quote = "You speak of dreams."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Two", character = "Morgan Elsbeth", quote = "Threads of fate do not lie."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Two", character = "Huyang", quote = "Your aptitude for the force falls short of them."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Three", character = "Ahsoka Tano", quote = "Learning to wield the force takes a deeper commitment."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Three", character = "Ahsoka Tano", quote = "I don’t need Sabine to be a Jedi, I need her to be herself."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Three", character = "Hera Syndulla", quote = "Were you ever in the war senator?"),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Five", character = "Anakin Skywalker", quote = "Live or die?"),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Five", character = "Anakin Skywalker", quote = "One is never too old to learn, Snips."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Five", character = "Ahsoka Tano", quote = "I choose to live."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Six", character = "Great Mothers", quote = "Welcome Child of Dathomir, you do our ancestors great credit."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Six", character = "Morgan Elsbeth", quote = "Your vision guided me across the stars."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Six", character = "Baylan Skoll", quote = "The fall of the Jedi, rise of the Empire. It repeats again and again and again."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Six", character = "Thrawn", quote = "What was first just a dream has become a frightening reality for those who may oppose us."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Six", character = "Thrawn", quote = "The desire to be reunited with a long lost friend."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Six", character = "Thrawn", quote = "You have gambled the fate of your galaxy in that belief."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Six", character = "Enoch", quote = "Be warned, nomads wonder this wasted land and prey on each other for survival."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Six", character = "Baylan Skoll", quote = "Comes from a breed of Bokken Jedi, trained in the wild after the Temple fell."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Seven", character = "Hera Syndulla", quote = "We have to prepare for the worst and hope for the best."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Seven", character = "Thrawn", quote = "We will always be one step ahead of her."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Seven", character = "Baylan Skoll", quote = "Your ambition drives you in one direction, my path lies in another."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Seven", character = "Baylan Skoll", quote = "Impatience for victory will guarantee defeat."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Eight", character = "Great Mothers", quote = "You shall be rewarded, the gift of shadows."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Eight", character = "Great Mothers", quote = "The Blade of Talzin."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Eight", character = "Huyang", quote = "Old enough to know that the relationship between a master and an apprentice is as challenging as it is meaningful."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Eight", character = "Ahsoka Tano", quote = "Over the years I’ve made my share of difficult choices."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Eight", character = "Ahsoka Tano", quote = "He always stood by me."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Eight", character = "Ahsoka Tano", quote = "Train your mind. Train your body. Trust in the Force."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Eight", character = "Thrawn", quote = "We cannot underestimate the apprentice of Anakin Skywalker."),
        QuoteContent(show = "Ahsoka", season = 1, episode = "Part Eight", character = "Thrawn", quote = "One wonders how similar you might become.")
    )

    transaction {
        Quotes.batchInsert(quotes){
            (show, season, episode, character, quote) ->
            val characterId = Characters.select {Characters.name eq character}.singleOrNull()?.get(Characters.id)
            if(characterId != null){
                this[Quotes.show] = show
                this[Quotes.season] = season
                this[Quotes.episode] = episode
                this[Quotes.characterId] = characterId
                this[Quotes.quote] = quote
            }
        }
    }
}

//Initializing the Quotes Facade
val quotesDAO = QuotesDaoFacadeImpl().apply {
    insertQuotes()
}
