package com.api.dao.quotes

import com.api.dao.DatabaseFactory.dbQuery
import com.api.models.Quote
import com.api.models.Quotes
import com.api.models.QuoteContent
import com.api.models.Characters
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.random.Random
import kotlin.random.nextInt

class QuotesDaoFacadeImpl : QuotesDaoFacade {
    private fun resultRowToQuote(row: ResultRow):Quote {
        val characterName = Characters.select { Characters.id eq row[Quotes.characterId] }.singleOrNull()?.get(Characters.name)
        return Quote(
        id = row[Quotes.id],
        show = row[Quotes.show],
        season = row[Quotes.season],
        episode = row[Quotes.episode],
        character = characterName ?: "Unknown character",
        quote = row[Quotes.quote],
        )
    }

    override suspend fun randomQuote(): Quote? {
        return dbQuery{
            val numberOfQuotes = Quotes.selectAll().count()
            val randomNumber = Random.nextInt(1..numberOfQuotes.toInt())
            Quotes.select { Quotes.id eq randomNumber }.map(::resultRowToQuote).singleOrNull()
        }
    }

    override suspend fun quotesByCharacter(character: String): List<Quote> {
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
                insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToQuote)
            }
            else {
                null
            }
        }
    }

    override suspend fun removeQuote( id: Int ) = dbQuery { Quotes.deleteWhere { Quotes.id eq id } > 0 }

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
        QuoteContent("The Mandalorian", 1, "Chapter 1", "The Mandalorian", "I can bring you in warm or I can bring you in cold."),
        QuoteContent("The Mandalorian", 1, "Chapter 1", "The Mandalorian", "I like those odds."),
        QuoteContent("The Mandalorian", 1, "Chapter 1", "Kuiil", "I have spoken."),
        QuoteContent("The Mandalorian", 1, "Chapter 1", "Kuiil", "You are a Mandalorian! Your ancestors rode the great Mythosaur. Surely you can ride this young foal."),
        QuoteContent("The Mandalorian", 1, "Chapter 1", "Kuiil", "They do not belong here. Those that live here come to seek peace. There will be no peace until they're gone."),
        QuoteContent("The Mandalorian", 1, "Chapter 1", "The Client", "Bounty hunting is a complicated profession."),
        QuoteContent("The Mandalorian", 1, "Chapter 1", "The Client", "Greef Karga said you were coming."),
        QuoteContent("The Mandalorian", 1, "Chapter 1", "The Client", "He said you were the best in the parsec. He also said you were expensive. Very expensive."),
        QuoteContent("The Mandalorian", 1, "Chapter 1", "Dr. Pershing", "That is not what we agreed upon."),
        QuoteContent("The Mandalorian", 1, "Chapter 1", "IG-11", "I will initiate self-destruct."),
        QuoteContent("The Mandalorian", 1, "Chapter 1", "IG-11", "You are a Guild member? I thought I was the only one on assignment."),
        QuoteContent("The Mandalorian", 1, "Chapter 1", "IG-11", "I will, of course, receive the reputation merits associated with the mission."),
        QuoteContent("The Mandalorian", 1, "Chapter 2", "The Mandalorian", "I’m a Mandalorian. Weapons are part of my religion."),
        QuoteContent("The Mandalorian", 1, "Chapter 2", "The Mandalorian", "I'm not gonna trade anything. These are my parts. They stole from me."),
        QuoteContent("The Mandalorian", 1, "Chapter 2", "Kuiil", "Thank you for bringing peace to my valley. And good luck with the child. May it survive and bring you a handsome reward."),
        QuoteContent("The Mandalorian", 1, "Chapter 3", "The Client", "How uncharacteristic of one of your reputation. You have taken both commission and payment. Is it not the code of the Guild that these events are now forgotten? That Beskar is enough to make a handsome replacement for your armor. Unfortunately, finding a Mandalorian in these trying times is more difficult than finding the steel."),
        QuoteContent("The Mandalorian", 1, "Chapter 3", "Greef Karga", "They all hate you, Mando. Because you're a legend!"),
        QuoteContent("The Mandalorian", 1, "Chapter 3", "Dr. Pershing", "I-I protected him. I protected him. If it wasn't for me, he would already be dead! Please! Please. Please."),
        QuoteContent("The Mandalorian", 1, "Chapter 3", "Paz Vizsla", "Get out of here! We'll hold 'em off!"),
        QuoteContent("The Mandalorian", 1, "Chapter 3", "The Mandalorian", "You're going to have to relocate the covert."),
        QuoteContent("The Mandalorian", 1, "Chapter 3", "The Armorer", "When one chooses to walk the Way of the Mandalore, you are both hunter and prey."),
        QuoteContent("The Mandalorian", 1, "Chapter 3", "The Armorer", "This is the way."),
        QuoteContent("The Mandalorian", 1, "Chapter 4", "The Mandalorian", "Stop touching things."),
        QuoteContent("The Mandalorian", 1, "Chapter 4", "Cara Dune", "It's gonna break his little heart."),
        QuoteContent("The Mandalorian", 1, "Chapter 4", "The Mandalorian", "Bad news. You can’t live here anymore."),
        QuoteContent("The Mandalorian", 1, "Chapter 4", "Cara Dune", "Well, let's just call it an early retirement. Look, I knew you were Guild. I figured you had a fob on me. That's why I came at you so hard."),
        QuoteContent("The Mandalorian", 1, "Chapter 5", "Toro Calican", "Bringin' you in will make me a full member of the Bounty Hunters' Guild."),
        QuoteContent("The Mandalorian", 1, "Chapter 5", "Fennec Shand", "The Mandalorian. His armor alone is worth more than my bounty."),
        QuoteContent("The Mandalorian", 1, "Chapter 5", "Fennec Shand", "Like I said, you don't see many. You bring the Guild that traitor, and they'll welcome you with open arms. Your name will be legendary."),
        QuoteContent("The Mandalorian", 1, "Chapter 5", "Toro Calican", "Picked up this bounty puck before I left the Mid-Rim. Fennec Shand, an assassin. Heard she's been on the run ever since the New Republic put all her employers in lockdown."),
        QuoteContent("The Mandalorian", 1, "Chapter 5", "The Mandalorian", "She's got the high ground. She'll wait for us to make the first move. I'm gonna rest. You take the first watch. Stay low!"),
        QuoteContent("The Mandalorian", 1, "Chapter 5", "Peli Motto", "Now, here's the plan. I am going to look after you until The Mandalorian gets back, and then I'm gonna charge him extra for watching you."),
        QuoteContent("The Mandalorian", 1, "Chapter 5", "Peli Motto", "You damage one of my droids, you'll pay for it."),
        QuoteContent("The Mandalorian", 1, "Chapter 5", "Peli Motto", "It's okay. You woke it up. Do you have any idea how long it took me to get it to sleep?"),
        QuoteContent("The Mandalorian", 1, "Chapter 6", "Ranzar Malk", "Yeah, one of our associates ran afoul of some competitors and got himself caught. So I'm puttin' together a crew to spring him. It's a five-person job. I got four. All I need is the ride, and you brought it."),
        QuoteContent("The Mandalorian", 1, "Chapter 6", "Xi'an", "Tell me why I shouldn't cut you down where you stand?"),
        QuoteContent("The Mandalorian", 1, "Chapter 6", "Mayfeld", "You better be good for it."),
        QuoteContent("The Mandalorian", 1, "Chapter 6", "The Mandalorian", "We're not killing anybody, you understand?"),
        QuoteContent("The Mandalorian", 1, "Chapter 6", "Mayfeld", "Get that blaster out of my face, Mando."),
        QuoteContent("The Mandalorian", 1, "Chapter 6", "Mayfeld", "Me, I was never into pets. Yeah, I didn't have the temperament. Patience, you know? I mean, I tried, but never worked out. But I'm thinkin' maybe I'll try again."),
        QuoteContent("The Mandalorian", 1, "Chapter 7", "The Mandalorian", "I've run into some problems."),
        QuoteContent("The Mandalorian", 1, "Chapter 7", "Kuiil", "I figured as much. Why else would you return?"),
        QuoteContent("The Mandalorian", 1, "Chapter 7", "Kuiil", "I'm not suited for such work. I can re-program IG-11 for nursing and protocol."),
        QuoteContent("The Mandalorian", 1, "Chapter 7", "Greef Karga", "There's something you should know. The plan was to kill you and take the kid. But after what happened last night, I couldn't go through with it. Go on. You can gun me down here and now and it wouldn't violate the Code. But if you do, this child will never be safe."),
        QuoteContent("The Mandalorian", 1, "Chapter 7", "Cara Dune", "So, we're going to Nevarro?"),
        QuoteContent("The Mandalorian", 1, "Chapter 7", "The Mandalorian", "Hard to tell. No insignia anymore. I took out the safehouse when I snatched the kid. More Imps have reinforced since."),
        QuoteContent("The Mandalorian", 1, "Chapter 7", "Moff Gideon", "Have they brought the child?"),
        QuoteContent("The Mandalorian", 1, "Chapter 8", "Cara Dune", "Is there another way out?"),
        QuoteContent("The Mandalorian", 1, "Chapter 8", "The Mandalorian", "The Mandalorians have a covert down in the sewers. If we can get down there, they can help us escape."),
        QuoteContent("The Mandalorian", 1, "Chapter 8", "The Mandalorian", "I was a foundling. They raised me in the Fighting Corps. I was treated as one of their own. When I came of age, I was sworn to the Creed. The only record of my family name was in the registers of Mandalore. Moff Gideon was an ISB officer during the purge. That's how I know it's him."),
        QuoteContent("The Mandalorian", 1, "Chapter 8", "The Armorer", "You are a clan of two."),
        QuoteContent("The Mandalorian", 1, "Chapter 8", "The Armorer", "It was not his fault. We revealed ourselves. We knew what could happen if we left the covert. The Imperials arrived shortly thereafter. This is what resulted."),
        QuoteContent("The Mandalorian", 1, "Chapter 8", "The Armorer", "Some may have escaped off-world."),
        QuoteContent("The Mandalorian", 1, "Chapter 8", "IG-11", "They will not be satisfied with anything less than the child. This is unacceptable. I will eliminate the enemy and you will escape."),
        QuoteContent("The Mandalorian", 1, "Chapter 8", "Greef Karga", "Come on, baby! Do the magic hand thing."),
        QuoteContent("The Mandalorian", 1, "Chapter 8", "The Mandalorian", "You protect the child. I can hold them back long enough for you to escape. Let me have a warrior's death."),
        QuoteContent("The Mandalorian", 1, "Chapter 8", "Cara Dune", "I won't leave you."),
        QuoteContent("The Mandalorian", 1, "Chapter 8", "Moff Gideon", "If you're asking if you can trust me, you cannot. Just as you betrayed our business arrangement, I would gladly break any promise and watch you die at my hand. The assurance I give is this: I will act in my own self-interest, which at this time involves your cooperation and benefit."),
        QuoteContent("The Mandalorian", 1, "Chapter 8", "Moff Gideon", "Your astute panic suggests that you understand your situation. I would prefer to avoid any further violence, and encourage a moment of consideration."),
        QuoteContent("The Mandalorian", 2, "Chapter 9", "The Mandalorian", "Where I go, he goes."),
        QuoteContent("The Mandalorian", 2, "Chapter 9", "Peli Motto", "Care for me to watch this wrinkled critter while you seek out adventure?"),
        QuoteContent("The Mandalorian", 2, "Chapter 9", "The Mandalorian", "I've been quested to bring this one back to its kind."),
        QuoteContent("The Mandalorian", 2, "Chapter 9", "Peli Motto", "Okay. This is a map of Tatooine before the war. You got Mos Eisley, Mos Espa, and up around this region, Mos Pelgo."),
        QuoteContent("The Mandalorian", 2, "Chapter 9", "Cobb Vanth", "What brings you here, stranger?"),
        QuoteContent("The Mandalorian", 2, "Chapter 9", "Cobb Vanth", "They look to me to protect 'em. But a krayt dragon is too much for me to take on alone. Help me kill it, I'll give you the armor."),
        QuoteContent("The Mandalorian", 2, "Chapter 9", "Cobb Vanth", "These monsters can't be reasoned with."),
        QuoteContent("The Mandalorian", 2, "Chapter 9", "The Mandalorian", "The same thing I'm telling you. If we fight amongst ourselves, the monster will kill us all."),
        QuoteContent("The Mandalorian", 2, "Chapter 10", "Peli Motto", "She needs her eggs fertilized by the equinox or her line will end. If you jump into hyperspace, they'll die. She said her husband has settled on the estuary moon of Trask in the system of the gas giant Kol Iben."),
        QuoteContent("The Mandalorian", 2, "Chapter 10", "Peli Motto", "What can I say? I'm an excellent judge of character."),
        QuoteContent("The Mandalorian", 2, "Chapter 10", "Peli Motto", "All right. He says the contact will rendezvous at the hangar. They'll tell where to find some Mandalorians. That's what you wanted, right?"),
        QuoteContent("The Mandalorian", 2, "Chapter 10", "The Mandalorian", "I’m sorry, lady. I don’t understand frog."),
        QuoteContent("The Mandalorian", 2, "Chapter 10", "Frog Lady", "I thought honoring one's word was a part of the Mandalorian code. I guess those are just stories for children."),
        QuoteContent("The Mandalorian", 2, "Chapter 10", "Carson Teva", "Razor Crest, stand down. We will fire. I repeat, we will fire."),
        QuoteContent("The Mandalorian", 2, "Chapter 10", "The Mandalorian", "Am I under arrest?"),
        QuoteContent("The Mandalorian", 2, "Chapter 10", "Carson Teva", "Technically, you should be. But these are trying times."),
        QuoteContent("The Mandalorian", 2, "Chapter 11", "The Mandalorian", "Where did you get that armor?"),
        QuoteContent("The Mandalorian", 2, "Chapter 11", "Bo-Katan Kryze", "This armor has been in my family for three generations."),
        QuoteContent("The Mandalorian", 2, "Chapter 11", "Axe Woves", "He's one of them."),
        QuoteContent("The Mandalorian", 2, "Chapter 11", "Bo-Katan Kryze", "I am Bo-Katan of Clan Kryze. I was born on Mandalore and fought the Purge. I am the last of my line. And you are a Child of the Watch."),
        QuoteContent("The Mandalorian", 2, "Chapter 11", "Bo-Katan Kryze", "Children of the Watch are a cult of religious zealots that broke away from the Mandalore society. Their goal was to re-establish the ancient way."),
        QuoteContent("The Mandalorian", 2, "Chapter 11", "The Mandalorian", "There is only one way. The Way of the Mandalore."),
        QuoteContent("The Mandalorian", 2, "Chapter 11", "Bo-Katan Kryze", "Trask is a black market port. They're staging weapons that have been bought and sold with the plunders of our planet. We're seizing these weapons and using them to retake our home world. Once we've done that, we'll seat a new Mandalore on the throne."),
        QuoteContent("The Mandalorian", 2, "Chapter 11", "Bo-Katan Kryze", "The Darksaber. Does he have it?"),
        QuoteContent("The Mandalorian", 2, "Chapter 11", "Bo-Katan Kryze", "Take the foundling to the city of Calodon on the forest planet of Corvus. There you will find Ahsoka Tano. Tell her you were sent by Bo-Katan. And thank you. Your bravery will not be forgotten. This is the Way."),
        QuoteContent("The Mandalorian", 2, "Chapter 11", "Bo-Katan Kryze", "Mandalorians are stronger together."),
        QuoteContent("The Mandalorian", 2, "Chapter 12", "The Mandalorian", "Looks like you two have been busy."),
        QuoteContent("The Mandalorian", 2, "Chapter 12", "Greef Karga", "I myself have been steeped in clerical work. Marshal Dune here is to be thanked for cleaning up the town."),
        QuoteContent("The Mandalorian", 2, "Chapter 12", "Cara Dune", "Dank farrik."),
        QuoteContent("The Mandalorian", 2, "Chapter 12", "Cara Dune", "This is Nevarro. We're here. This entire area's a green zone. Completely safe. But over on this side is the problem."),
        QuoteContent("The Mandalorian", 2, "Chapter 12", "Cara Dune", "No, this isn't a military operation. This is a lab. We need to get into the system and figure out what's going on."),
        QuoteContent("The Mandalorian", 2, "Chapter 12", "Cara Dune", "He'll be fine here. You have my word."),
        QuoteContent("The Mandalorian", 2, "Chapter 12", "Dr. Pershing", "I highly doubt we'll find a donor with a higher M-count, though."),
        QuoteContent("The Mandalorian", 2, "Chapter 12", "Dr. Pershing", "The child is small, and I was only able to harvest a limited amount without killing him. If these experiments are to continue as requested, we would again require access to the donor. I will not disappoint you again, Moff Gideon."),
        QuoteContent("The Mandalorian", 2, "Chapter 12", "Carson Teva", "You did a hell of a job cleaning up the system. According to records, you're quite a soldier. We could really use you."),
        QuoteContent("The Mandalorian", 2, "Chapter 13", "Morgan Elsbeth", "Come forward. You're a Mandalorian?"),
        QuoteContent("The Mandalorian", 2, "Chapter 13", "Morgan Elsbeth", "I have a proposition that may interest you."),
        QuoteContent("The Mandalorian", 2, "Chapter 13", "Morgan Elsbeth", "One that you are well-suited for. The Jedi are the ancient enemy of Mandalore."),
        QuoteContent("The Mandalorian", 2, "Chapter 13", "Morgan Elsbeth", "Pure beskar, like your armor. Kill the Jedi and it's yours."),
        QuoteContent("The Mandalorian", 2, "Chapter 13", "The Mandalorian", "Ahsoka Tano! Bo-Katan sent me. We need to talk."),
        QuoteContent("The Mandalorian", 2, "Chapter 13", "Ahsoka Tano", "I hope it's about him."),
        QuoteContent("The Mandalorian", 2, "Chapter 13", "Ahsoka Tano", "Grogu and I can feel each other’s thoughts."),
        QuoteContent("The Mandalorian", 2, "Chapter 13", "Ahsoka Tano", "He was raised at the Jedi Temple on Coruscant. Many Masters trained him over the years. At the end of the Clone Wars when the Empire rose to power, he was hidden. Someone took him from the Temple. Then his memories becomes dark. He seemed lost. Alone. I've known one other being like this. A wise Jedi Master named Yoda."),
        QuoteContent("The Mandalorian", 2, "Chapter 13", "Ahsoka Tano", "He's formed a strong attachment to you. I cannot train him."),
        QuoteContent("The Mandalorian", 2, "Chapter 13", "Ahsoka Tano", "I’ve seen what such feelings can do to a fully trained Jedi Knight. To the best of us."),
        QuoteContent("The Mandalorian", 2, "Chapter 13", "Ahsoka Tano", "The Force is what gives him his powers. It is an energy field created by all living things. To wield it takes a great deal of training and discipline."),
        QuoteContent("The Mandalorian", 2, "Chapter 13", "The Mandalorian", "I've seen him do things I can't explain. My task was to bring him to a Jedi."),
        QuoteContent("The Mandalorian", 2, "Chapter 13", "The Mandalorian", "A Mandalorian and a Jedi? They'll never see it coming."),
        QuoteContent("The Mandalorian", 2, "Chapter 13", "Ahsoka Tano", "You're like a father to him. I cannot train him."),
        QuoteContent("The Mandalorian", 2, "Chapter 13", "Ahsoka Tano", "Go to the planet Tython. You will find the ancient ruins of a temple that has a strong connection to the Force. Place Grogu on the seeing stone at the top of the mountain."),
        QuoteContent("The Mandalorian", 2, "Chapter 13", "Ahsoka Tano", "May the Force be with you."),
        QuoteContent("The Mandalorian", 2, "Chapter 14", "The Mandalorian", "Oh, come on, kid. Ahsoka told me all I had to do was get you here and you'd do the rest."),
        QuoteContent("The Mandalorian", 2, "Chapter 14", "Boba Fett", "I don’t want your armor. I want my armor that you got from Cobb Vanth back on Tatooine. It belongs to me."),
        QuoteContent("The Mandalorian", 2, "Chapter 14", "Boba Fett", "I'm a simple man making his way through the galaxy. Like my father before me."),
        QuoteContent("The Mandalorian", 2, "Chapter 14", "Boba Fett", "Beskar. I want you to take a look at something. My chain code has been encoded in this armor for 25 years. You see, this is me. Boba Fett. This is my father, Jango Fett."),
        QuoteContent("The Mandalorian", 2, "Chapter 14", "Fennec Shand", "You look like you've just seen a ghost."),
        QuoteContent("The Mandalorian", 2, "Chapter 14", "Boba Fett", "She was left for dead on the sands of Tatooine, as was I. But fate sometimes steps in to rescue the wretched."),
        QuoteContent("The Mandalorian", 2, "Chapter 14", "Moff Gideon", "You've gotten very good with that. But it makes you oh-so sleepy. Have you ever seen one of these?"),
        QuoteContent("The Mandalorian", 2, "Chapter 14", "Moff Gideon", "Oh, uh-uh-uh. You're not ready to play with such things. Liable to put an eye out with one of these. Looks like you could use a nice, long sleep."),
        QuoteContent("The Mandalorian", 2, "Chapter 14", "Boba Fett", "Until he has returned to you safely, we are in your debt."),
        QuoteContent("The Mandalorian", 2, "Chapter 15", "Cara Dune", "Let's go! I've got a job for you."),
        QuoteContent("The Mandalorian", 2, "Chapter 15", "The Mandalorian", "We need coordinates for Moff Gideon's cruiser."),
        QuoteContent("The Mandalorian", 2, "Chapter 15", "Cara Dune", "They have his kid."),
        QuoteContent("The Mandalorian", 2, "Chapter 15", "Cara Dune", "The little green guy?"),
        QuoteContent("The Mandalorian", 2, "Chapter 15", "Mayfeld", "I can't get those coordinates unless I have access to an internal Imperial terminal. I believe there's one on Morak."),
        QuoteContent("The Mandalorian", 2, "Chapter 15", "Mayfeld", "It's a secret Imperial mining hub, okay? If you can get me in there, I can get you the coordinates."),
        QuoteContent("The Mandalorian", 2, "Chapter 15", "Boba Fett", "I did an initial scan of the planet. This is what you're talkin' about, right?"),
        QuoteContent("The Mandalorian", 2, "Chapter 15", "Boba Fett", "Looks like rhydonium. Highly volatile and explosive."),
        QuoteContent("The Mandalorian", 2, "Chapter 15", "Cara Dune", "Wish I could say it looked good on you, but I'd be lying."),
        QuoteContent("The Mandalorian", 2, "Chapter 15", "The Mandalorian", "Let's get one thing straight. You and I are nothing alike."),
        QuoteContent("The Mandalorian", 2, "Chapter 15", "Mayfeld", "You did what you had to do. I never saw your face."),
        QuoteContent("The Mandalorian", 2, "Chapter 15", "The Mandalorian", "Moff Gideon, You have something I want. You may think you have some idea of what you're in possession of, but you do not. Soon, he will be back with me. He means more to me than you will ever know."),
        QuoteContent("The Mandalorian", 2, "Chapter 16", "Bo-Katan Kryze", "We will help you. In exchange, we will keep that ship to retake Mandalore. If you should manage to finish your quest, I would have you reconsider joining our efforts. Mandalorians have been in exile from our homeworld for far too long."),
        QuoteContent("The Mandalorian", 2, "Chapter 16", "Bo-Katan Kryze", "One more thing. Gideon has a weapon that once belonged to me. It is an ancient weapon that can cut through anything."),
        QuoteContent("The Mandalorian", 2, "Chapter 16", "Koska Reeves", "Almost anything."),
        QuoteContent("The Mandalorian", 2, "Chapter 16", "The Mandalorian", "Help me rescue the Child, and you can have whatever you want. He is my only priority."),
        QuoteContent("The Mandalorian", 2, "Chapter 16", "Dr. Pershing", "There's a garrison of dark troopers on board. They're the ones who abducted the Child."),
        QuoteContent("The Mandalorian", 2, "Chapter 16", "Cara Dune", "He brought him in alive, that's what happened. And now the New Republic's gonna have to double the payment."),
        QuoteContent("The Mandalorian", 2, "Chapter 16", "Moff Gideon", "The Darksaber. It belongs to you."),
        QuoteContent("The Mandalorian", 2, "Chapter 16", "Moff Gideon", "She can't take it. It must be won in battle. In order for her to wield the Darksaber again, she would need to defeat you in combat."),
        QuoteContent("The Mandalorian", 2, "Chapter 16", "The Mandalorian", "Come on, just take it."),
        QuoteContent("The Mandalorian", 2, "Chapter 16", "Cara Dune", "One X-wing? Great. We're saved."),
        QuoteContent("The Mandalorian", 2, "Chapter 16", "Moff Gideon", "A friendly piece of advice, assume that I know everything."),
        QuoteContent("The Mandalorian", 2, "Chapter 16", "Luke Skywalker", "Come, little one."),
        QuoteContent("The Mandalorian", 2, "Chapter 16", "The Mandalorian", "I’ll see you again. I promise."),
        QuoteContent("The Mandalorian", 2, "Chapter 16", "Luke Skywalker", "May the Force be with you."),
        QuoteContent("The Mandalorian", 2, "Chapter 16", "Bib Fortuna", "Boba. I thought you were dead. I am so glad to see you. I had heard many rumors."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 1", "8D8", "Presenting Dokk Strassi, leader of the Trandoshan family, protectors of the city center and its business territories."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 1", "Boba Fett", "That's weird. I used to work for him."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 1", "Boba Fett", "You were loyal to both your bosses. Would you be loyal to me if I were to spare you?"),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 1", "Garsa Fwip", "Welcome to the Sanctuary. Would you care to partake in any of our sundry offerings?"),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 1", "Boba Fett", "I'm just here to introduce myself and assure you that your, uh, business will continue to thrive under my watchful eye."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 1", "Fennec Shand", "Huh. Yours look shinier than mine."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 1", "Mok Shaiz's Majordomo", "The matter of tribute."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 1", "Boba Fett", "What? I’m the crime lord. He’s supposed to pay me."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 1", "Fennec Shand", "Shall I kill him?"),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 1", "Boba Fett", "Jabba ruled with fear. I intend to rule with respect."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 2", "Boba Fett", "You shouldn't have to hide. You are warriors."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 2", "Boba Fett", "You have machines now, too. And you know every grain of sand in the Dune Sea."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 2", "Boba Fett", "These sands are no longer free for you to pass. These people lay ancestral claim to the Dune Sea, and if you are to pass, a toll is to be paid to them. Any death dealt from the passing freighters will be returned tenfold. Now, go back to your syndicate and present these terms. Your lives are a gesture of our civility. Now walk. Single file, in the direction of the high sun. It will lead you to Anchorhead by sunset if you leave now."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 2", "Mayor Mok Shaiz", "Who is this who enters unannounced?"),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 2", "Mok Shaiz's Majordomo", "it is the new Daimyo, Boba Fett, Your Excellence."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 2", "Mayor Mok Shaiz", "It was Jabba the Hutt's throne."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 2", "Mayor Mok Shaiz", "I know that you sit on the throne of your former employer."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 2", "Boba Fett", "Yes. And now it is mine. And I will take this payment as what you should have brought me as tribute. You should remember, you serve as long as the Daimyo of Tatooine deem it so."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 2", "Boba Fett", "You can bring as many gladiators as you wish but these are not the death pits of Duur and I am not a sleeping Trandoshan guard. This territory is mine. Go back to Nal Hutta."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 2", "Hutt Twins", "Sleep lightly, bounty hunter."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 2", "Boba Fett", "Like a bantha."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 3", "Skad", "If you're a Daimyo, then why'd you let the monger charge us a month's wages for a week's water?"),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 3", "Drash", "There is no work, mighty Daimyo. Look around you."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 3", "Boba Fett", "Then you will work for me. You got guts, I'll give you that. You better fight as good as you talk dank."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 3", "Rancor Keeper", "They're powerful fighters, so that is what most know. But they form strong bonds with their owners. It is said that the Witches of Dathomir even rode them through the forest and fens."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 3", "Fennec Shand", "If you wish to continue breathing, I advise you to weigh your next words carefully."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 3", "Mok Shaiz's Majordomo", "He's with the Pykes. The Mayor's gone. He... he's working with the Pykes."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 3", "Boba Fett", "No hard feelings. It's just business. Take it from an ex-bounty hunter, don't work for scugholes. It's not worth it."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 3", "Skad", "I know a Pyke when I see one."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 3", "Fennec Shand", "They arrived on the starliner."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 4", "Boba Fett", "I may sit on that throne, but I have no designs on any of your territories. I ask for no tribute or quarter, and I expect to give none, either. I'm here to make a proposal that's mutually beneficial. As I'm sure you all know, the Pyke Syndicate are mustering troops in Mos Espa. They have slowly absorbed our planet as part of their spice trade. They have bribed the Mayor and are draining Tatooine of its wealth."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 4", "Boba Fett", "They may be stubborn, but they are not foolish enough to see that the Pykes would eventually take over the whole planet. Either way, we must prepare for war."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 4", "Boba Fett", "I have plenty of credits. What I'm short on is muscle."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 4", "Boba Fett", "Find other banthas. Make baby banthas. Go!"),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 4", "Boba Fett", "Do you know who I am? I am Boba Fett."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 4", "Boba Fett", "You can only get so far without a tribe."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 4", "Boba Fett", "You are Master Assassin Fennec Shand of the Mid Rim."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 4", "Fennec Shand", "Bib Fortuna took over his territory. And now he rules from that palace. If the ship is yours, why don't you just ask for it back?"),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 4", "Boba Fett", "Yes. If I'm gonna start a house, I need brains and muscle. You have both."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 4", "Boba Fett", "The Sarlacc Pit. That's where I was trapped all those years ago. That's where I'll find my armor."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 4", "Fennec Shand", "In there? It's dissolved."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 5", "The Mandalorian", "I have met Jedi."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 5", "The Armorer", "Then you have completed your quest."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 5", "The Armorer", "Bo-Katan is a cautionary tale. She once laid claim to rule Mandalore based purely on blood and the sword you now possess. But it was gifted to her and not won by Creed. Bo-Katan Kryze was born of a mighty house, but they lost sight of the way."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 5", "Paz Vizsla", "Maybe the Darksaber belongs in someone else's hands."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 5", "The Armorer", "Din Djarin, have you ever removed your helmet? Have you ever removed your helmet? By Creed, you must vow."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 5", "The Armorer", "Then, you are a Mandalorian no more."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 5", "The Armorer", "According to Creed, one may only be redeemed in the living waters beneath the mines of Mandalore."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 5", "The Mandalorian", "Loyalty and solidarity are the way."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 5", "Peli Motto", "So, where's your unlikely companion?"),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 5", "Peli Motto", "Dated a Jawa for a while. They’re quite furry. Very furry."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 5", "Peli Motto", "Hang on a second. Do you have any idea what this is? This is an N-1 starfighter, handmade for the royal guard and commissioned personally by the Queen of Naboo."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 5", "The Mandalorian", "Dank farrik, she's fast."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 5", "Carson Teva", "Your voice is mighty familiar. Did you used to fly a Razor Crest?"),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 5", "The Mandalorian", "I think you have the wrong guy, officer."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 5", "Fennec Shand", "By any chance, are you looking for work?"),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 5", "The Mandalorian", "Boba Fett."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 5", "The Mandalorian", "Tell him it's on the house. But first, I got to pay a visit to a little friend."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 6", "Ahsoka Tano", "I'm an old friend of the family."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 6", "The Mandalorian", "I thought you weren't going to help train Grogu."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 6", "Ahsoka Tano", "There's nothing now, but will someday be a great school. Grogu will be its first student."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 6", "Luke Skywalker", "Get back up. Always get back up."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 6", "Luke Skywalker", "The galaxy is a dangerous place, Grogu. I will teach you to protect yourself."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 6", "Luke Skywalker", "I want to tell you about someone you remind me of a great deal. His name was Yoda. He was small like you, but his heart was huge, and the Force was strong in him. He once said to me, \"Size matters not.\" That's how he talked. He would speak in riddles. Have you ever heard anyone talk like that back home? Do you remember back home?"),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 6", "Ahsoka Tano", "I warned you when we met that your attachment to Grogu would be difficult to let go of."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 6", "The Mandalorian", "He was a Mandalorian foundling in my care. I just wanna make sure he's safe."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 6", "Luke Skywalker", "The Mandalorian was here."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 6", "Ahsoka Tano", "As I told you. The two share a strong bond, and he brought him a gift."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 6", "Luke Skywalker", "If you choose the armor, you'll return to your friend, the Mandalorian. However, you will be giving in to attachment to those that you love and forsaking the way of the Jedi."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 6", "Ahsoka Tano", "So much like your father."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 6", "Cobb Vanth", "The peace is intact, Mando. We took out that dragon. My people don't want to fight no more."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 6", "Cad Bane", "Whatever Fett is paying you, we'll match, and all you've got to do is stay put and let things play out."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 6", "Cad Bane", "I’d be careful where I was sticking my nose if I were you."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 6", "Cad Bane", "Boba Fett is a cold-blooded killer who worked with the Empire."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 6", "Cobb Vanth", "You tell your spice runners Tatooine is closed for business. This planet's seen enough violence."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 6", "Cad Bane", "Tatooine belongs to the Syndicate. As long as the spice keeps running, everyone will be left alone."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 7", "Boba Fett", "Watch out! We need reinforcements."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 7", "The Mandalorian", "From where? You've run out of friends."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 7", "The Mandalorian", "We’ll both die in the name of honor."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 7", "Cad Bane", "Let the spice move through Mos Espa, and all this can be avoided."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 7", "Cad Bane", "You mean the one that massacred your Tusken family and blamed it on a speed bike gang? You know it's true."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 7", "Boba Fett", "Tell your client negotiations are terminated."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 7", "Cad Bane", "You're going soft in your old age."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 7", "Pyke Boss", "The Syndicate forces have pulled back from Mos Espa and should be arriving here in Mos Eisley shortly so that we may disembark."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 7", "Peli Motto", "Nice head-tails. Come on. Get behind me, pretty face. Peli's got you covered."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 7", "Peli Motto", "Hey, Mando! Look who's here."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 7", "The Mandalorian", "Oh! Okay, little guy. I'm happy to see you, too. I didn't know when I'd see you again. It's okay. Yeah. I missed you, too, buddy. But, uh... we're in a bit of a bind here right now."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 7", "Cad Bane", "You gave it a shot. You tried to go straight. But you've got your father's blood pumping through your veins. You're a killer. This isn't the first time I beat you out on a job. There's no shame in it."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 7", "Peli Motto", "Don't worry, kid. Your old man's crafty."),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 7", "Boba Fett", "This is my city!"),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 7", "Boba Fett", "Why must everyone bow at me?"),
        QuoteContent("The Book of Boba Fett", 1, "Chapter 7", "Fennec Shand", "If not us, then who?"),
        QuoteContent("The Mandalorian", 3, "Chapter 17", "The Armorer", "I shall walk the way of the Mand’alore."),
        QuoteContent("The Mandalorian", 3, "Chapter 17", "The Armorer", "You have removed your helmet. What's worse, you did so of your own free will. You are no longer Mandalorian."),
        QuoteContent("The Mandalorian", 3, "Chapter 17", "The Mandalorian", "The Creed teaches us of redemption."),
        QuoteContent("The Mandalorian", 3, "Chapter 17", "The Mandalorian", "If I visit the planet and I can bring you proof that I have bathed in the Living Waters beneath the mines of Mandalore, then by Creed. the decree of exile will be lifted and I would redeemed."),
        QuoteContent("The Mandalorian", 3, "Chapter 17", "Greef Karga", "I can set you up with a prime tract right over by the hot springs. You and the little one, you can settle down, hang up your blaster. Live off the fat of the land."),
        QuoteContent("The Mandalorian", 3, "Chapter 17", "The Mandalorian", "His name is Grogu."),
        QuoteContent("The Mandalorian", 3, "Chapter 17", "Greef Karga", "Oh. I'm confused. I thought you had completed your mission, but you're still running around here with the same little critter."),
        QuoteContent("The Mandalorian", 3, "Chapter 17", "The Mandalorian", "His name is Grogu."),
        QuoteContent("The Mandalorian", 3, "Chapter 17", "The Anzellan", "No squeezie."),
        QuoteContent("The Mandalorian", 3, "Chapter 17", "The Mandalorian", "Being a Mandalorian's not just learing about how to fight."),
        QuoteContent("The Mandalorian", 3, "Chapter 17", "Bo-Katan Kryze", "When I returned without the Darksaber, my forces melted away."),
        QuoteContent("The Mandalorian", 3, "Chapter 17", "The Mandalorian", " I am going to Mandalore so that I may bathe in the Living Waters and be forgiven for my transgressions."),
        QuoteContent("The Mandalorian", 3, "Chapter 17", "Bo-Katan Kryze", "You are a fool. There's nothing magic about the mines of Mandalore. They supplied beskar ore to our ancestors and the rest is superstition. That planet has been ravaged, plundered, and poisoned."),
        QuoteContent("The Mandalorian", 3, "Chapter 17", "The Mandalorian", "Never trust a pirate."),
        QuoteContent("The Mandalorian", 3, "Chapter 18", "Peli Motto", "Where's my guy?"),
        QuoteContent("The Mandalorian", 3, "Chapter 18", "Peli Motto", "Me? You know, I do have a life. Big holiday. I had big plans! You know, I don't just sit around here and work all day. I'm very popular."),
        QuoteContent("The Mandalorian", 3, "Chapter 18", "The Mandalorian", "It's Mandalore, the homeworld of our people. Every Mandalorian can trace their roots back to this planet, and the beskar mines deep within. And you know what? I've never been there, either."),
        QuoteContent("The Mandalorian", 3, "Chapter 18", "The Mandalorian", "I grew up there. On that moon. Concordia. And that's Kalevala where we visited Bo-Katan. It's in the same system. A Mandalorian has to understand maps and know their way around. That way, you'll never be lost."),
        QuoteContent("The Mandalorian", 3, "Chapter 18", "Bo-Katan Kryze", "Let’s get rid of him once and for all."),
        QuoteContent("The Mandalorian", 3, "Chapter 18", "The Mandalorian", "Without the Creed, what are we? What do we stand for? Our people are scatted like stars in the galaxy. The Creed is how we survived. You rescued me and I'll always be in your debt. But I can't go with you until I fulfill my obligation."),
        QuoteContent("The Mandalorian", 3, "Chapter 18", "The Mandalorian", "Mandalore is not cursed."),
        QuoteContent("The Mandalorian", 3, "Chapter 18", "Bo-Katan Kryze", "A great society is now a memory. I once ruled here for a brief time. Now, it's destroyed. Nothing to cling to but ashes."),
        QuoteContent("The Mandalorian", 3, "Chapter 19", "Elia Kane", "I try not to think about him anymore. Thanks to the rehabilitation program, I can contribute to the New Republic. Just like you."),
        QuoteContent("The Mandalorian", 3, "Chapter 19", "Dr. Pershing", "Um... I guess maybe those, uh, yellow travel biscuits from the ration packs?."),
        QuoteContent("The Mandalorian", 3, "Chapter 19", "Elia Kane", "Allowed? Live a little, Doc. It's not the Empire."),
        QuoteContent("The Mandalorian", 3, "Chapter 19", "Elia Kane", "The New Republic is trying their best, but they're struggling. There are lots of capable people who wanna help."),
        QuoteContent("The Mandalorian", 3, "Chapter 19", "Dr. Pershing", "What we were talking about the other day. My research. I've been thinking about it. I know it's important, and in the hands of the New Republic, it can actually be used for good. I just need to prove it."),
        QuoteContent("The Mandalorian", 3, "Chapter 19", "Bo-Katan Kryze", "The bombings from the Purge must have triggered seismic activities. Did you see anything alive?"),
        QuoteContent("The Mandalorian", 3, "Chapter 19", "The Mandalorian", "I'm bringing you to a Mandalorian covert. This is how we have survived in exile."),
        QuoteContent("The Mandalorian", 3, "Chapter 19", "Paz Vizsla", "Come no further. You are an apostate, Din Djarin."),
        QuoteContent("The Mandalorian", 3, "Chapter 19", "Paz Vizsla", "And who are you, Nite Owl?"),
        QuoteContent("The Mandalorian", 3, "Chapter 19", "Paz Vizsla", "Din Djarin claims to have bathed in the Living Waters."),
        QuoteContent("The Mandalorian", 3, "Chapter 19", "The Armorer", "He speaks the truth. These are indeed the Living Waters. Din Djarin, you are redeemed. This is the Way. And Bo-Katan Kryze, by Creed, you too are redeemed."),
        QuoteContent("The Mandalorian", 3, "Chapter 20", "The Armorer", "The Mythosaur belongs to all Mandalorians. It is always acceptable to wear."),
        QuoteContent("The Mandalorian", 3, "Chapter 20", "Bo-Katan Kryze", "What would you say if I told you I saw one?"),
        QuoteContent("The Mandalorian", 3, "Chapter 20", "The Armorer", "When you choose the walk the Way of the Mandalore, you will see many things."),
        QuoteContent("The Mandalorian", 3, "Chapter 20", "The Mandalorian", "If he is ever to rise from foundling to apprentice, he must learn."),
        QuoteContent("The Mandalorian", 3, "Chapter 20", "Bo-Katan Kryze", "How do you eat when other people are around?"),
        QuoteContent("The Mandalorian", 3, "Chapter 20", "The Mandalorian", "You don't. When you get your food, you go off to find a place where you can take off your helmet."),
        QuoteContent("The Mandalorian", 3, "Chapter 20", "Paz Vizsla", "You are the leader of the war party. You have the honor of staying by the fire. This is the Way."),
        QuoteContent("The Mandalorian", 3, "Chapter 20", "The Armorer", "Bo-Katan Kryze, you have honored your house and all of Mandalore. You have done the highest honor of the Creed. Saving a foundling."),
        QuoteContent("The Mandalorian", 3, "Chapter 21", "Carson Teva", "I knew it. He never made it to trial. There don't appear to be any survivors. And Moff Gideon's body is missing. This was an extraction."),
        QuoteContent("The Mandalorian", 3, "Chapter 21", "Elia Kane", "I have. I spent some time there, in fact. They have yet to sign the Charter. They're not a member planet."),
        QuoteContent("The Mandalorian", 3, "Chapter 21", "Carson Teva", "What does that matter? We can't leave them defenseless."),
        QuoteContent("The Mandalorian", 3, "Chapter 21", "Carson Teva", "You and your sort didn't \"see the light.\" You were captured."),
        QuoteContent("The Mandalorian", 3, "Chapter 21", "Carson Teva", "No. I was liberated."),
        QuoteContent("The Mandalorian", 3, "Chapter 21", "Paz Vizsla", "Clear out, Blue Boy. The New Republic isn't welcome here."),
        QuoteContent("The Mandalorian", 3, "Chapter 21", "Carson Teva", "Greef Karga sent this holo-message. Nevarro is under siege by pirates. He's asking for help."),
        QuoteContent("The Mandalorian", 3, "Chapter 21", "Carson Teva", "I can't say for sure, but something doesn't smell right. Look, it's not your fight. I just came to tell you, your friend is in danger and I thought you should know."),
        QuoteContent("The Mandalorian", 3, "Chapter 21", "The Mandalorian", "Now, many of you don't know Greef Karga. And those that do fought against him when you rescued me from his ambush many cycles ago on the streets of Nevarro. Since then, he's had a change of heart and has risked his life to save mine as well as the foundling in my charge."),
        QuoteContent("The Mandalorian", 3, "Chapter 21", "The Mandalorian", "Perhaps it is time for us to live in the light once again on a planet where we are welcome. So our culture may flourish and our children can feel what it is to play in the sunlight."),
        QuoteContent("The Mandalorian", 3, "Chapter 21", "Paz Vizsla", "The question we should be asking ourselves, \"Why? Why should we lay our lives down yet again?\" Because we are Mandalorians."),
        QuoteContent("The Mandalorian", 3, "Chapter 21", "The Armorer", "We must walk the Way together. All Mandalorians."),
        QuoteContent("The Mandalorian", 3, "Chapter 21", "The Armorer", "Bo-Katan Kryze is going off to bring other Mandalorians in exile to us so that we may join together once again."),
        QuoteContent("The Mandalorian", 3, "Chapter 21", "The Armorer", "Bo-Katan walks both worlds. And she can bring all tribes together. It is time to retake Mandalore."),
        QuoteContent("The Mandalorian", 3, "Chapter 22", "Captain Bombardier", "Let's address the bantha in the room. I was once a facilities planning officer during the war. And thanks to the New Republic Amnesty Program, I was able to help rebuild Plazir-15."),
        QuoteContent("The Mandalorian", 3, "Chapter 22", "The Mandalorian", "You were Imperial?"),
        QuoteContent("The Mandalorian", 3, "Chapter 22", "The Duchess", "He was. Plazir suffered greatly under Imperial rule. My husband came here as part of his rehabilitation. He saw the rebuilding of this planet on which my family served as nobility since it was originally settled, and... we fell in love."),
        QuoteContent("The Mandalorian", 3, "Chapter 22", "The Duchess", "Could I perhaps hold the baby? Please?"),
        QuoteContent("The Mandalorian", 3, "Chapter 22", "The Duchess", "Is this true?"),
        QuoteContent("The Mandalorian", 3, "Chapter 22", "Captain Bombardier", "Despicable."),
        QuoteContent("The Mandalorian", 3, "Chapter 22", "Axe Woves", "You'll never be the true leader of our people. You won't even take the Darksaber from him. He's the one you should be challenging."),
        QuoteContent("The Mandalorian", 3, "Chapter 22", "Bo-Katan Kryze", "Enough Mandalorian blood has been spilled by our own hands."),
        QuoteContent("The Mandalorian", 3, "Chapter 22", "Bo-Katan Kryze", "Din Djarin took the Creed and chose to walk the Way, just as our ancestors did. He is every bit of Mandalorian that they were. Certainly as much as any of us."),
        QuoteContent("The Mandalorian", 3, "Chapter 22", "Axe Woves", "But according to our ways, the ruler of Mandalore must possess the Darksaber."),
        QuoteContent("The Mandalorian", 3, "Chapter 22", "The Mandalorian", "While exploring Mandalore, I was captured, and this blade was taken from me. Bo Katan rescued me and slayed my captor. She defeated the enemy that defeated me. Would this blade not belong to her?"),
        QuoteContent("The Mandalorian", 3, "Chapter 24", "Koska Reeves", "Do you live here?"),
        QuoteContent("The Mandalorian", 3, "Chapter 24", "Bo-Katan Kryze", "I've only ever seen gardens in the domed cities. I never knew the surface could still sustain plant life."),
        QuoteContent("The Mandalorian", 3, "Chapter 24", "Bo-Katan Kryze", "Moff Gideon is alive. He's gathered his forces and is using our home world as his base. They're sending up fighters to destroy the fleet."),
        QuoteContent("The Mandalorian", 3, "Chapter 24", "Moff Gideon", "My clones were finally going to be perfect. The best parts of me but improved by adding the one thing I never had: the Force."),
        QuoteContent("The Mandalorian", 3, "Chapter 24", "The Mandalorian", "Grogu, I'm going to need you to be brave for me, okay? We can't keep running. If we don't take out Moff Gideon, this will never end. You with me?"),
        QuoteContent("The Mandalorian", 3, "Chapter 24", "Bo-Katan Kryze", "I've got this. Go save your kid."),
        QuoteContent("The Mandalorian", 3, "Chapter 24", "Moff Gideon", "Hand over the Darksaber and I will give you a warrior's death."),
        QuoteContent("The Mandalorian", 3, "Chapter 24", "Moff Gideon", "The Darksaber is gone. You've lost everything. Mandalorians are weak once they lose their trinkets."),
        QuoteContent("The Mandalorian", 3, "Chapter 24", "Bo-Katan Kryze", "Mandalorians are stronger together."),
        QuoteContent("The Mandalorian", 3, "Chapter 24", "The Mandalorian", "Grogu is my apprentice. He is no longer a foundling. Add him to the Song."),
        QuoteContent("The Mandalorian", 3, "Chapter 24", "The Armorer", "Let it be written in Song that Din Djarin is accepting this foundling as his son. You are now Din Grogu, Mandalorian apprentice."),
        QuoteContent("The Mandalorian", 3, "Chapter 24", "The Mandalorian", "I have a business proposition."),
        QuoteContent("The Mandalorian", 3, "Chapter 24", "The Mandalorian", "You don't have the resources to protect the Outer Rim, let alone hunt down Imperial remnants. And I need work."),
        QuoteContent("The Mandalorian", 3, "Chapter 24", "Carson Teva", "Let me get this straight. You want to work for the New Republic."),
        QuoteContent("The Mandalorian", 3, "Chapter 24", "IG-11", "Greetings, citizens. I am IG-11, your new Marshal. Your new Marshal of Nevarro. I am here to serve and protect the citizenry."),
        QuoteContent("Ahsoka", 1, "Part One", "Ahsoka Tano", "Let's just say I didn't follow standard Jedi protocol."),
        QuoteContent("Ahsoka", 1, "Part One", "Baylan Skoll", "You're right about one thing, captain. We are no Jedi."),
        QuoteContent("Ahsoka", 1, "Part One", "Ahsoka Tano", "Sometimes the right reasons have the wrong consequences."),
        QuoteContent("Ahsoka", 1, "Part One", "Ahsoka Tano", "This isn't just about finding Ezra. It’s about preventing another war."),
        QuoteContent("Ahsoka", 1, "Part One", "Ezra Bridger", "I’m counting on you to see this through."),
        QuoteContent("Ahsoka", 1, "Part One", "Ezra Bridger", "As a Jedi, sometimes you have to make the decision no one else can."),
        QuoteContent("Ahsoka", 1, "Part Two", "Hera Syndulla", "You both need to help each other."),
        QuoteContent("Ahsoka", 1, "Part Two", "Baylan Skoll", "You speak of dreams."),
        QuoteContent("Ahsoka", 1, "Part Two", "Morgan Elsbeth", "Threads of fate do not lie."),
        QuoteContent("Ahsoka", 1, "Part Two", "Morgan Elsbeth", "Thrawn calls to me. Across time and space."),
        QuoteContent("Ahsoka", 1, "Part Two", "Huyang", "Your aptitude for the force falls short of them."),
        QuoteContent("Ahsoka", 1, "Part Three", "Ahsoka Tano", "Learning to wield the force takes a deeper commitment."),
        QuoteContent("Ahsoka", 1, "Part Three", "Ahsoka Tano", "I don’t need Sabine to be a Jedi, I need her to be herself."),
        QuoteContent("Ahsoka", 1, "Part Three", "Jacen Syndulla", "I want to be a Jedi!"),
        QuoteContent("Ahsoka", 1, "Part Three", "Hera Syndulla", "Were you ever in the war senator?"),
        QuoteContent("Ahsoka", 1, "Part Four", "Sabine Wren", "Don't worry about me."),
        QuoteContent("Ahsoka", 1, "Part Four", "Anakin Skywalker", "Hello Snips."),
        QuoteContent("Ahsoka", 1, "Part Four", "Ahsoka Tano", "Master?"),
        QuoteContent("Ahsoka", 1, "Part Four", "Anakin Skywalker", "I didn't expect to see you so soon."),
        QuoteContent("Ahsoka", 1, "Part Five", "Ahsoka Tano", "You look the same."),
        QuoteContent("Ahsoka", 1, "Part Five", "Anakin Skywalker", "You look old."),
        QuoteContent("Ahsoka", 1, "Part Five", "Ahsoka Tano", "Well, that happens."),
        QuoteContent("Ahsoka", 1, "Part Five", "Anakin Skywalker", "I'm here to finish your training."),
        QuoteContent("Ahsoka", 1, "Part Five", "Anakin Skywalker", "One is never too old to learn, Snips."),
        QuoteContent("Ahsoka", 1, "Part Five", "Anakin Skywalker", "All right. What's the lesson, Master?"),
        QuoteContent("Ahsoka", 1, "Part Five", "Anakin Skywalker", "Live or die."),
        QuoteContent("Ahsoka", 1, "Part Five", "Ahsoka Tano", "I won't fight you."),
        QuoteContent("Ahsoka", 1, "Part Five", "Anakin Skywalker", "I've heard that before."),
        QuoteContent("Ahsoka", 1, "Part Five", "Anakin Skywalker", "You've learned nothing. Back to the beginning."),
        QuoteContent("Ahsoka", 1, "Part Five", "Ahsoka Tano", "Is that all I'll have to teach my own Padawan someday? How to fight?"),
        QuoteContent("Ahsoka", 1, "Part Five", "Anakin Skywalker", "You lack conviction!"),
        QuoteContent("Ahsoka", 1, "Part Five", "Anakin Skywalker", "Time to die!"),
        QuoteContent("Ahsoka", 1, "Part Five", "Ahsoka Tano", "I choose to live."),
        QuoteContent("Ahsoka", 1, "Part Five", "Jacen Syndulla", "There's something out there, mom. I can feel it."),
        QuoteContent("Ahsoka", 1, "Part Six", "Great Mothers", "Welcome Child of Dathomir, you do our ancestors great credit."),
        QuoteContent("Ahsoka", 1, "Part Six", "Morgan Elsbeth", "Your vision guided me across the stars."),
        QuoteContent("Ahsoka", 1, "Part Six", "Thrawn", "What was first just a dream has become a frightening reality for those who may oppose us."),
        QuoteContent("Ahsoka", 1, "Part Six", "Sabine Wren", "Where's Ezra?"),
        QuoteContent("Ahsoka", 1, "Part Six", "Thrawn", "The desire to be reunited with your long-lost friend. How that singular focus will reshape our galaxy."),
        QuoteContent("Ahsoka", 1, "Part Six", "Thrawn", "You helped my cause. Now I shall help yours. You should know, though, that once my starship departs, you'll be stranded here forever. It's also quite possible that your friend is dead."),
        QuoteContent("Ahsoka", 1, "Part Six", "Sabine Wren", "If you survived, I'm sure he's doing just fine."),
        QuoteContent("Ahsoka", 1, "Part Six", "Thrawn", "You have gambled the fate of your galaxy in that belief."),
        QuoteContent("Ahsoka", 1, "Part Six", "Enoch", "Be warned, nomads wonder this wasted land and prey on each other for survival."),
        QuoteContent("Ahsoka", 1, "Part Six", "Shin Hati", "What is it, Master?"),
        QuoteContent("Ahsoka", 1, "Part Six", "Baylan Skoll", "This is the land of dreams and madness. Children's stories come to life."),
        QuoteContent("Ahsoka", 1, "Part Six", "Baylan Skoll", "You weren't raised at the Temple. Stories of this galaxy are considered folktales. Some ancient past, long forgotten."),
        QuoteContent("Ahsoka", 1, "Part Six", "Shin Hati", "With good reason. Sometime stories are just stories."),
        QuoteContent("Ahsoka", 1, "Part Six", "Baylan Skoll", "The fall of the Jedi, rise of the Empire. It repeats again and again and again."),
        QuoteContent("Ahsoka", 1, "Part Six", "Baylan Skoll", "Comes from a breed of Bokken Jedi, trained in the wild after the Temple fell."),
        QuoteContent("Ahsoka", 1, "Part Six", "Ezra Bridger", "I knew I could count on you. Though, it sure took you long enough."),
        QuoteContent("Ahsoka", 1, "Part Six", "Sabine Wren", "Well, you didn't exactly tell any of us where you were going."),
        QuoteContent("Ahsoka", 1, "Part Six", "Huyang", "Intergalactic travel within a star whale. Now I really have done it all."),
        QuoteContent("Ahsoka", 1, "Part Seven", "Anakin Skywalker", "Don't be afraid. Just remember what I taught you, and trust your instincts. I know you can do this, Ahsoka."),
        QuoteContent("Ahsoka", 1, "Part Seven", "Hera Syndulla", "We have to prepare for the worst and hope for the best."),
        QuoteContent("Ahsoka", 1, "Part Seven", "Thrawn", "We will always be one step ahead of her."),
        QuoteContent("Ahsoka", 1, "Part Seven", "Baylan Skoll", "Your ambition drives you in one direction, my path lies in another."),
        QuoteContent("Ahsoka", 1, "Part Seven", "Baylan Skoll", "One parting lessons, Shin: impatience for victory will guarantee defeat."),
        QuoteContent("Ahsoka", 1, "Part Eight", "Great Mothers", "You shall be rewarded, the gift of shadows."),
        QuoteContent("Ahsoka", 1, "Part Eight", "Great Mothers", "The Blade of Talzin."),
        QuoteContent("Ahsoka", 1, "Part Eight", "Huyang", "Old enough to know that the relationship between a master and an apprentice is as challenging as it is meaningful."),
        QuoteContent("Ahsoka", 1, "Part Eight", "Ahsoka Tano", "Over the years I’ve made my share of difficult choices."),
        QuoteContent("Ahsoka", 1, "Part Eight", "Ahsoka Tano", "He always stood by me."),
        QuoteContent("Ahsoka", 1, "Part Eight", "Ahsoka Tano", "Train your mind. Train your body. Trust in the Force."),
        QuoteContent("Ahsoka", 1, "Part Eight", "Thrawn", "We cannot underestimate the apprentice of Anakin Skywalker."),
        QuoteContent("Ahsoka", 1, "Part Eight", "Thrawn", "One wonders how similar you might become.")
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

val quotesDAO = QuotesDaoFacadeImpl().apply {
    insertQuotes()
}