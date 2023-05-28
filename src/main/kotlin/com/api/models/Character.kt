package com.api.models

import kotlinx.serialization.Serializable

@Serializable
data class Character(val id: Int, val name: String)
val charactersStorage = listOf<Character>(
    Character(1, "The Mandalorian"),
    Character(2, "Grogu"),
    Character(3, "The Amorer"),
    Character(4, "Captain Bombardier"),
    Character(5, "Cad Bane"),
    Character(6, "Cara Dune"),
    Character(7, "Boba Fett"),
    Character(8, "Frog Lady"),
    Character(9, "Bib Fortuna"),
    Character(10, "Garsa Fwip"),
    Character(11, "Moff Gideon"),
    Character(12, "IG-11"),
    Character(13, "Greef Karga"),
    Character(14, "Bo-Katan Kryze")
)