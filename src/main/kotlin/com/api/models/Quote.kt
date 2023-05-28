package com.api.models

import kotlinx.serialization.Serializable

@Serializable
data class Quote(val id:Int, val quote: String, val season: Int, val chapter: String, val character: String)
val quotesStorage = listOf<Quote>(
    Quote(1, "I can bring you in warm or I can bring you in cold.", 1, "Chapter 1", "The Mandalorian"),
    Quote(2, "This is the way.", 1, "Chapter 3", "The Armorer"),
    Quote(3, "I like those odds.", 1, "Chapter 1", "The Mandalorian"),
    Quote(4, "I have spoken.", 1, "Chapter 1", "Kuiil"),
    Quote(5, "Bounty hunting is a complicated profession.", 1, "Chapter 1", "The Client"),
    Quote(6, "I will initiate self-destruct", 1, "Chapter 1", "IG-11"),
    Quote(7, "I’m a Mandalorian. Weapons are part of my religion.", 1, "Chapter 2", "The Mandalorian"),
    Quote(8, "They all hate you, Mando. Because you're a legend!", 1, "Chapter 3", "Greef Karga"),
    Quote(9, "When one chooses to walk the Way of the Mandalore, you are both hunter and prey.", 1, "Chapter 3", "The Armorer"),
    Quote(10, "Stop touching things.", 1, "Chapter 4", "The Mandalorian"),
    Quote(11, "Bad news. You can’t live here anymore.", 1, "Chapter 4", "The Mandalorian"),
    Quote(12, "Your name will be legendary", 1, "Chapter 5", "Fennec Shand"),
    Quote(13, "You are a clan of two.", 1, "Chapter 8", "The Armorer"),
    Quote(14, "Come on, baby! Do the magic hand thing.", 1, "Chapter 8", "Greef Karga"),
    Quote(15, "Where I go, he goes.", 2, "Chapter 9", "The Mandalorian"),
    Quote(16, "I guess every once in a while both suns shine on a womp rat’s tail.", 2, "Chapter 9", "Cobb Vanth"),
    Quote(17, "I’m sorry, lady. I don’t understand frog.", 2, "Chapter 10", "The Mandalorian"),
    Quote(18, "Mandalorians are stronger together.", 2, "Chapter 11", "Bo-Katan Kryze"),
    Quote(19, "There you will find Ahsoka Tano. Tell her you were sent by Bo-Katan.", 2, "Chapter 12", "Bo-Katan Kyrze"),
    Quote(20, "Dank farrik.", 2, "Chapter 12", "Cara Dune"),
    Quote(21, "Ahsoka Tano! Bo-Katan sent me. We need to talk.", 2, "Chapter 13", "The Mandalorian"),
    Quote(22, "Grogu and I can feel each other’s thoughts.", 2, "Chapter 13", "Ahsoka Tano"),
    Quote(23, "I’ve seen what such feelings can do to a fully trained Jedi Knight. To the best of us.", 2, "Chapter 13", "Ahsoka Tano"),
    Quote(24, "I don’t want your armor. I want my armor.", 2, "Chapter 14", "Boba Fett"),
    Quote(25, "A friendly piece of advice, assume that I know everything.", 2, "Chapter 16", "Moff Gideon"),
    Quote(26, "Come, little one.", 2, "Chapter 16", "Luke Skywalker"),
    Quote(27, "I’ll see you again. I promise.", 2, "Chapter 16", "The Mandalorian"),
    )
