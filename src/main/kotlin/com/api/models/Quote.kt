package com.api.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Quote(val id:Int, val show: String, val season: Int, val episode: String, val character: String, val quote: String)

// creates an anonymous object that inherits the table class
object Quotes: Table(){
    val id = integer("id").autoIncrement()
    val show = varchar("show", 25)
    val season = integer("season")
    val episode =  varchar("episode", 20)
    val character = varchar("character", 20)
    val quote = varchar("quote", 1024)


    override val primaryKey = PrimaryKey(id)
}

val quotesStorage = mutableListOf<Quote>(
    Quote(id = 1, show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "Din Djarin", quote = "I can bring you in warm or I can bring you in cold."),
    Quote(id = 2, show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "Din Djarin", quote = "I like those odds."),
    Quote(id = 3, show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "Kuiil", quote = "I have spoken."),
    Quote(id = 4, show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "The Client", quote = "Bounty hunting is a complicated profession."),
    Quote(id = 6, show = "The Mandalorian", season = 1, episode = "Chapter 1", character = "IG-11", quote = "I will initiate self-destruct."),
    Quote(id = 7, show = "The Mandalorian", season = 1, episode = "Chapter 2", character = "Din Djarin", quote = "I’m a Mandalorian. Weapons are part of my religion."),
    Quote(id = 8, show = "The Mandalorian", season = 1, episode = "Chapter 3", character = "Greef Karga", quote = "They all hate you, Mando. Because you're a legend!"),
    Quote(id = 9, show = "The Mandalorian", season = 1, episode = "Chapter 3", character = "The Armorer", quote = "When one chooses to walk the Way of the Mandalore, you are both hunter and prey."),
    Quote(id = 10, show = "The Mandalorian", season = 1, episode = "Chapter 3", character = "The Armorer", quote = "This is the way."),
    Quote(id = 11, show = "The Mandalorian", season = 1, episode = "Chapter 4", character = "Din Djarin", quote = "Stop touching things."),
    Quote(id = 12, show = "The Mandalorian", season = 1, episode = "Chapter 4", character = "Din Djarin", quote = "Bad news. You can’t live here anymore."),
    Quote(id = 13, show = "The Mandalorian", season = 1, episode = "Chapter 5", character = "Fennec Shand", quote = "Your name will be legendary."),
    Quote(id = 14, show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "The Armorer", quote = "You are a clan of two."),
    Quote(id = 15, show = "The Mandalorian", season = 1, episode = "Chapter 8", character = "Greef Karga", quote = "Come on, baby! Do the magic hand thing."),
    Quote(id = 16, show = "The Mandalorian", season = 2, episode = "Chapter 9", character = "Din Djarin", quote = "Where I go, he goes."),
    Quote(id = 17, show = "The Mandalorian", season = 2, episode = "Chapter 9", character = "Cobb Vanth", quote = "I guess every once in a while both suns shine on a womp rat’s tail."),
    Quote(id = 18, show = "The Mandalorian", season = 2, episode = "Chapter 10", character = "Din Djarin", quote = "I’m sorry, lady. I don’t understand frog."),
    Quote(id = 19, show = "The Mandalorian", season = 2, episode = "Chapter 11", character = "Bo-Katan Kryze", quote = "Mandalorians are stronger together."),
    Quote(id = 20, show = "The Mandalorian", season = 2, episode = "Chapter 12", character = "Bo-Katan Kryze", quote = "There you will find Ahsoka Tano. Tell her you were sent by Bo-Katan."),
    Quote(id = 21, show = "The Mandalorian", season = 2, episode = "Chapter 12", character = "Cara Dune", quote = "Dank farrik."),
    Quote(id = 22, show = "The Mandalorian", season = 2, episode = "Chapter 13", character = "Din Djarin", quote = "Ahsoka Tano! Bo-Katan sent me. We need to talk."),
    Quote(id = 23, show = "The Mandalorian", season = 2, episode = "Chapter 13", character = "Ahsoka Tano", quote = "Grogu and I can feel each other’s thoughts."),
    Quote(id = 24, show = "The Mandalorian", season = 2, episode = "Chapter 13", character = "Ahsoka Tano", quote = "I’ve seen what such feelings can do to a fully trained Jedi Knight. To the best of us."),
    Quote(id = 25, show = "The Mandalorian", season = 2, episode = "Chapter 14", character = "Boba Fett", quote = "I don’t want your armor. I want my armor."),
    Quote(id = 26, show = "The Mandalorian", season = 2, episode = "Chapter 16", character = "Moff Gideon", quote = "A friendly piece of advice, assume that I know everything."),
    Quote(id = 27, show = "The Mandalorian", season = 2, episode = "Chapter 16", character = "Luke Skywalker", quote = "Come, little one."),
    Quote(id = 28, show = "The Mandalorian", season = 2, episode = "Chapter 16", character = "Din Djarin", quote = "I’ll see you again. I promise."),
    Quote(id = 29, show = "The Book of Boba Fett", season = 1, episode = "Chapter 1", character = "Boba Fett", quote = "I’m the crime lord. He’s supposed to pay me."),
    Quote(id = 30, show = "The Book of Boba Fett", season = 1, episode = "Chapter 1", character = "Fennec Shand", quote = "Shall I kill him?"),
    Quote(id = 31, show = "The Book of Boba Fett", season = 1, episode = "Chapter 1", character = "Boba Fett", quote = "Jabba ruled with fear. I intend to rule with respect."),
    Quote(id = 32, show = "The Book of Boba Fett", season = 1, episode = "Chapter 2", character = "Hutt Twin", quote = "Sleep lightly, bounty hunter."),
    Quote(id = 33, show = "The Book of Boba Fett", season = 1, episode = "Chapter 2", character = "Boba Fett", quote = "Like a bantha."),
    Quote(id = 34, show = "The Book of Boba Fett", season = 1, episode = "Chapter 3", character = "Fennec Shand", quote = "If you wish to continue breathing, I advise you to weigh your next words carefully."),
    Quote(id = 35, show = "The Book of Boba Fett", season = 1, episode = "Chapter 3", character = "Boba Fett", quote = "No hard feelings. It’s just business."),
    Quote(id = 36, show = "The Book of Boba Fett", season = 1, episode = "Chapter 4", character = "Boba Fett", quote = "Find other banthas. Make baby banthas. Go!"),
    Quote(id = 37, show = "The Book of Boba Fett", season = 1, episode = "Chapter 4", character = "Boba Fett", quote = "Do you know who I am? I am Boba Fett."),
    Quote(id = 38, show = "The Book of Boba Fett", season = 1, episode = "Chapter 4", character = "Boba Fett", quote = "You can only get so far without a tribe."),
    Quote(id = 39, show = "The Book of Boba Fett", season = 1, episode = "Chapter 4", character = "Garsa Fwip", quote = "Hit it, Max."),
    Quote(id = 40, show = "The Book of Boba Fett", season = 1, episode = "Chapter 5", character = "Din Djarin", quote = "Loyalty and solidarity are the way."),
    Quote(id = 41, show = "The Book of Boba Fett", season = 1, episode = "Chapter 5", character = "Peli Motto", quote = "Dated a Jawa for a while. They’re quite furry. Very furry."),
    Quote(id = 42, show = "The Book of Boba Fett", season = 1, episode = "Chapter 6", character = "Luke Skywalker", quote = "Get back up. Always get back up."),
    Quote(id = 43, show = "The Book of Boba Fett", season = 1, episode = "Chapter 6", character = "Ahsoka Tano", quote = "So much like your father."),
    Quote(id = 44, show = "The Book of Boba Fett", season = 1, episode = "Chapter 6", character = "Cad Bane", quote = "I’d be careful where I was sticking my nose if I were you."),
    Quote(id = 45, show = "The Book of Boba Fett", season = 1, episode = "Chapter 7", character = "Din Djarin", quote = "We’ll both die in the name of honor."),
    Quote(id = 46, show = "The Book of Boba Fett", season = 1, episode = "Chapter 7", character = "Boba Fett", quote = "This is my city!"),
    Quote(id = 47, show = "The Book of Boba Fett", season = 1, episode = "Chapter 7", character = "Fennec Shand", quote = "If not us, then who?"),
    Quote(id = 48, show = "Ahsoka", season = 1, episode = "Part One", character = "Ahsoka Tano", quote = "Let's just say I didn't follow standard Jedi protocol."),
    Quote(id = 49, show = "Ahsoka", season = 1, episode = "Part One", character = "Ahsoka Tano", quote = "Sometimes the right reasons have the wrong consequences."),
    Quote(id = 50, show = "Ahsoka", season = 1, episode = "Part One", character = "Ahsoka Tano", quote = "This isn't just about finding Ezra. It’s about preventing another war."),
    Quote(id = 51, show = "Ahsoka", season = 1, episode = "Part One", character = "Ezra Bridger", quote = "I’m counting on you to see this through."),
    Quote(id = 52, show = "Ahsoka", season = 1, episode = "Part Two", character = "Hera Syndulla", quote = "You both need to help each other."),
    Quote(id = 53, show = "Ahsoka", season = 1, episode = "Part Two", character = "Baylan Skoll", quote = "You speak of dreams."),
    Quote(id = 54, show = "Ahsoka", season = 1, episode = "Part Two", character = "Morgan Elsbeth", quote = "Threads of fate do not lie."),
    Quote(id = 55, show = "Ahsoka", season = 1, episode = "Part Two", character = "Huyang", quote = "Your aptitude for the force falls short of them."),
    Quote(id = 56, show = "Ahsoka", season = 1, episode = "Part Three", character = "Ahsoka Tano", quote = "Learning to wield the force takes a deeper commitment."),
    Quote(id = 57, show = "Ahsoka", season = 1, episode = "Part Three", character = "Ahsoka Tano", quote = "I don’t need Sabine to be a Jedi, I need her to be herself."),
    Quote(id = 58, show = "Ahsoka", season = 1, episode = "Part Three", character = "Hera Syndulla", quote = "Were you ever in the war senator?"),
    Quote(id = 59, show = "Ahsoka", season = 1, episode = "Part Five", character = "Anakin Skywalker", quote = "Live or die?"),
    Quote(id = 60, show = "Ahsoka", season = 1, episode = "Part Five", character = "Anakin Skywalker", quote = "One is never too old to learn, Snips."),
    Quote(id = 61, show = "Ahsoka", season = 1, episode = "Part Five", character = "Ahsoka Tano", quote = "I choose to live."),
    Quote(id = 62, show = "Ahsoka", season = 1, episode = "Part Six", character = "Great Mothers", quote = "Welcome Child of Dathomir, you do our ancestors great credit."),
    Quote(id = 63, show = "Ahsoka", season = 1, episode = "Part Six", character = "Morgan Elsbeth", quote = "Your vision guided me across the stars."),
    Quote(id = 64, show = "Ahsoka", season = 1, episode = "Part Six", character = "Baylan Skoll", quote = "The fall of the Jedi, rise of the Empire. It repeats again and again and again."),
    Quote(id = 65, show = "Ahsoka", season = 1, episode = "Part Six", character = "Thrawn", quote = "What was first just a dream has become a frightening reality for those who may oppose us."),
    Quote(id = 66, show = "Ahsoka", season = 1, episode = "Part Six", character = "Thrawn", quote = "The desire to be reunited with a long lost friend."),
    Quote(id = 67, show = "Ahsoka", season = 1, episode = "Part Six", character = "Thrawn", quote = "You have gambled the fate of your galaxy in that belief."),
    Quote(id = 68, show = "Ahsoka", season = 1, episode = "Part Six", character = "Enoch", quote = "Be warned, nomads wonder this wasted land and prey on each other for survival."),
    Quote(id = 69, show = "Ahsoka", season = 1, episode = "Part Six", character = "Baylan Skoll", quote = "Comes from a breed of Bokken Jedi, trained in the wild after the Temple fell."),
    Quote(id = 70, show = "Ahsoka", season = 1, episode = "Part Seven", character = "Hera Syndulla", quote = "We have to prepare for the worst and hope for the best."),
    Quote(id = 71, show = "Ahsoka", season = 1, episode = "Part Seven", character = "Thrawn", quote = "We will always be one step ahead of her."),
    Quote(id = 72, show = "Ahsoka", season = 1, episode = "Part Seven", character = "Baylan Skoll", quote = "Your ambition drives you in one direction, my path lies in another."),
    Quote(id = 73, show = "Ahsoka", season = 1, episode = "Part Seven", character = "Baylan Skoll", quote = "Impatience for victory will guarantee defeat."),
    Quote(id = 74, show = "Ahsoka", season = 1, episode = "Part Eight", character = "Great Mothers", quote = "You shall be rewarded, the gift of shadows."),
    Quote(id = 75, show = "Ahsoka", season = 1, episode = "Part Eight", character = "Great Mothers", quote = "The Blade of Talzin."),
    Quote(id = 76, show = "Ahsoka", season = 1, episode = "Part Eight", character = "Huyang", quote = "Old enough to know that the relationship between a master and an apprentice is as challenging as it is meaningful."),
    Quote(id = 77, show = "Ahsoka", season = 1, episode = "Part Eight", character = "Ahsoka Tano", quote = "Over the years I’ve made my share of difficult choices."),
    Quote(id = 78, show = "Ahsoka", season = 1, episode = "Part Eight", character = "Ahsoka Tano", quote = "He always stood by me."),
    Quote(id = 79, show = "Ahsoka", season = 1, episode = "Part Eight", character = "Ahsoka Tano", quote = "Train your mind. Train your body. Trust in the Force."),
    Quote(id = 80, show = "Ahsoka", season = 1, episode = "Part Eight", character = "Thrawn", quote = "We cannot underestimate the apprentice of Anakin Skywalker."),
    Quote(id = 81, show = "Ahsoka", season = 1, episode = "Part Eight", character = "Thrawn", quote = "One wonders how similar you might become."))































