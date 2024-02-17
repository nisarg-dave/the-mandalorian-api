package com.api.dao.planets

import com.api.dao.DatabaseFactory.dbQuery
import com.api.models.*
import org.jetbrains.exposed.sql.ResultRow
import kotlin.random.Random
import kotlin.random.nextInt
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction


class PlanetsDaoFacadeImpl : PlanetsDaoFacade {
    private fun resultRowToPlanet(row: ResultRow):Planet = Planet(
        id = row[Planets.id],
        name = row[Planets.name],
        description = row[Planets.description],
        imgUrl = row[Planets.imgUrl]
    )

    override suspend fun randomPlanet(): Planet? {
        return dbQuery{
            val numberOfPlanets = Planets.selectAll().count()
            val randomNumber = Random.nextInt(1..numberOfPlanets.toInt())
            Planets.select{Planets.id eq randomNumber}.map(::resultRowToPlanet).singleOrNull()
        }
    }

    override suspend fun planetByName(planetName: String): Planet? {
        return dbQuery {
            Planets.select {Planets.name eq planetName}.map(::resultRowToPlanet).singleOrNull()
        }
    }

    override suspend fun addPlanet(name: String, description: String, imgUrl: String): Planet? {
        return dbQuery {
            val insertStatement = Planets.insert {
                it[this.name] = name
                it[this.description] = description
                it[this.imgUrl] = imgUrl
            }
            insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToPlanet)
        }
    }

    override suspend fun removePlanet(id: Int): Boolean {
        return dbQuery {
            Planets.deleteWhere { Planets.id eq id }
        } > 0
    }

    override suspend fun editPlanet(id: Int, name: String, description: String, imgUrl: String): Boolean {
        return dbQuery {
            Planets.update({Planets.id eq id}){
                it[this.name] = name
                it[this.description] = description
                it[this.imgUrl] = imgUrl
            }
        } > 0
    }
}

fun insertPlanets(){
    val planets = listOf(
        PlanetContent("Pagodon", "Pagodon is an ice-covered planet located in the Outer Rim Territories and is the home of Ravinak creatures. The Mandalorian arrived on Pagodon and captured a Mythrol at the bar after some resistance.", "https://static.wikia.nocookie.net/starwars/images/6/6b/Mandalorian_icy_planet.png/revision/latest?cb=20200103043931"),
        PlanetContent("Nevarro", "Nevarro is a volcanic planet located in the Outer Rim Territories. It is known for its lava fields and volcanic rivers. Initially, Nevarro was where the Bounty Hunter's guild operated from under Greef Karga. It eventually became a trade outpost.", "https://static.wikia.nocookie.net/starwars/images/5/5c/Nevarro-TMCh17.png/revision/latest?cb=20230423193402"),
        PlanetContent("Aq Vetina", "Aq Vetina is the home planet of The Mandalorian and is located in the Outer Rim Territories. During the Clone Wars, the Mandalorians of the Death Watch group rescued a young Din Djarin from Separatist droids.", "https://static.wikia.nocookie.net/starwars/images/2/20/Settlement_MandoEp8.png/revision/latest?cb=20220227203148"),
        PlanetContent("Arvala-7", "Arvala-7 is a remote desert planet located in the Outer Rim Territories. It was the home planet of the Ugnaught Kuill who lived on a moisture farm. The Mandalorian arrived on Arvala-7 to retrieve Grogu for The Client and later to recruit Kuiil for their mission on Nevarro.", "https://static.wikia.nocookie.net/starwars/images/6/6c/Arvala-7.png/revision/latest?cb=20220206070215"),
        PlanetContent("Tatooine", "Tatooine is a desert planet that is located in the Outer Rim Territories and orbits two suns. It is the home planet of Jawas, Tuskens and Anakin and Luke Skywalker. The Mandalorian and Grogu frequently visit Tatooine and it is home to many of their allies such as Peli Motto, Fennec Shand and Boba Fett.", "https://static.wikia.nocookie.net/starwars/images/b/b0/Tatooine_TPM.png/revision/latest?cb=20131019121937"),
        PlanetContent("Sorgan", "Sorgan is a forrest planet located in the Outer Rim Territories. It is the home to a Klatooinian and farming tribe. The Mandalorian and Cara Dune stopped the Klatooinian tribe from raiding the villagers and their farms.", "https://static.wikia.nocookie.net/starwars/images/9/9a/Sorgan.png/revision/latest?cb=20201227234057"),
        PlanetContent("Maldo Kreis", "Maldo Kreis is an ice planet located in the Outer Rim Territories in the Arkanis sector. The Mandalorian and the Frog Lady crash-landed on Maldo Kreis and were attacked by ice spiders. They were later rescued by the New Republic pilots.", "https://static.wikia.nocookie.net/starwars/images/4/4d/MaldoKreis.png/revision/latest?cb=20220302042100"),
        PlanetContent("Trask", "Trask is an estuary moon orbiting the gas giant Kol Iben in the Outer Rim Territories. It is known for its vast seas and busy port. The Mandalorian meets Bob-Katan kryze and her Nite Owls here while taking the Frog Lady to her husband. Together, they raid and takeover an Imperial cruiser on Trask.", "https://static.wikia.nocookie.net/starwars/images/b/b1/TraskMando.png/revision/latest?cb=20220302052141"),
        PlanetContent("Corvus", "Corvus is a forest planet in the Outer Rim Territories. It is where The Mandalorian and Grogu first meet Ahsoka Tano and where they first fight Morgan Elsbeth who was the Magistrate of the city of Calodan.", "https://static.wikia.nocookie.net/starwars/images/a/a2/Corvus-TMCh13.png/revision/latest?cb=20220306052703"),
        PlanetContent("Tython", "Tython is a terrestrial planet located in the Deep Core region. It is where Ahsoka Tano instructs The Mandalorian to take Grogu. Grogu had meditated upon the seeing stone at the temple on Tython before being abducted by Moff Gideon's dark troopers.", "https://static.wikia.nocookie.net/starwars/images/4/4d/Tython_TMS2.png/revision/latest?cb=20201205044714"),
        PlanetContent("Karthon", "Karthon is a prison moon located in the Outer Rim Territories where Imperial machinery is dismantled by inmates. It was where Migs Mayfeld was sentenced for fifty years before being set free by Cara Dune.", "https://static.wikia.nocookie.net/starwars/images/7/74/Karthon_Chop_Fields.jpg/revision/latest?cb=20201212035241"),
        PlanetContent("Morak", "Morak is a jungle planet located in the Outer Rim Territories. It housed a secret Rhydonium Imperial refinery which was later destroyed by Boba Fett after The Mandalorian and Mayfeld were able to retrieve the coordinates of Moff Gideon's cruiser.", "https://static.wikia.nocookie.net/starwars/images/1/14/MorakE15.png/revision/latest?cb=20201212045559"),
        PlanetContent("Glavis Ringwold", "Glavis Ringworld is a space station that encircled a small star. Day and night cycles artificially occur due to the sunlight that passed through the open loop. The Mandalorian visited Glavis Ringworld to find the hidden Mandalorian covert.", "https://static.wikia.nocookie.net/starwars/images/1/16/Glavis_BoBF.png/revision/latest?cb=20220130053325"),
        PlanetContent("Ossus", "Ossus is a terrestrial planet in the Adega system of the Outer Rim Territories. It is where Luke Skywalker constructed his Jedi temple and where he briefly trained Grogu.", "https://static.wikia.nocookie.net/starwars/images/3/3b/Ossus-FromTheDesertComesAStranger.png/revision/latest?cb=20220206205838"),
        PlanetContent("Unnamed Mandalorian Covert Hideout", "The Unnamed Mandalorian covert hideout planet is an unnamed planet where the Children of the Watch hid. It is known for its sandy beaches, jagged rock structures and deep caves.", "https://static.wikia.nocookie.net/starwars/images/4/4b/CovertObjectCh19.png/revision/latest?cb=20230315094641"),
        PlanetContent("Mandalore", "Mandalore is the home-world of Mandalorians and is located in the Outer Rim Territories. The planet was heavily bombed in the Night of a Thousand Tears by the Empire during the Great Purge of Mandalore, and millions of Mandalorians were killed, and the planet was thought to be uninhabitable. The fusion bombs and fusion rays used in the Purge crystallized the surface of the planet. During the New Republic Era, an Imperial remnant led by Moff Gideon established a secret base there but was later destroyed by the Mandalorians who reclaimed their home-world.", "https://static.wikia.nocookie.net/starwars/images/9/9d/MandaloreCh18.png/revision/latest?cb=20230308084552"),
        PlanetContent("Concordia", "Concordia is a moon of the planet Mandalore in the Mandalore system. It used to be a mining base during the Mandalore's wars but the mines were later abandoned. The mines were later used by Concordia's governor, Pre Vizsla, for the operation of the Death Watch group. The Mandalorian grew up on Concordia and him and other members of the Children of the Watch survived the Night of a Thousand Tears by hiding on Concordia.", "https://static.wikia.nocookie.net/starwars/images/8/85/Concordia_TMCh18.png/revision/latest?cb=20230312065314"),
        PlanetContent("Kalevala", "Kalevala is a planet located in the Mandalore system. It is the home-world of the House Kryze. The Mandalorian and Grogu visited Bo-Katan Kryze on Kalevala to find out the location of the Mines of Mandalore. Later, Kryze Castle was destroyed by Moff Gideon's TIE Interceptors and Bombers.", "https://static.wikia.nocookie.net/starwars/images/f/f9/Kalevala-Space.png/revision/latest?cb=20230302024844"),
        PlanetContent("Coruscant", "Coruscant is a city-covered planet in Coruscant system of the Core Worlds. It was the Imperial Center during the Galatic Empire. During the New Republic Era, The New Republic Defense Force maintained a presence on Coruscant, providing security and structure, while overseeing the decommission of Imperial Star Destroyers in salvage yards. Additionally, Coruscant was the center of the New Republic Amnesty Program which granted captured Imperials the chance to join the New Republic.", "https://static.wikia.nocookie.net/starwars/images/8/84/CoruscantGlobeE1.png/revision/latest?cb=20130123002137"),
        PlanetContent("Plazir-15", "Plazir-15 is located in the Outer Rim Territories and is known for its ponds, green plains and domed cities. It is ruled under a direct democracy by Captain Bombardier and the Duchess.", "https://static.wikia.nocookie.net/starwars/images/1/15/Plazir_15_MandoS3.png/revision/latest?cb=20230405084242"),
        PlanetContent("Adelphi", "Adelphi is a terrestrial planet that houses a New Republic outpost where Carson Teva and the Adelphi Rangers are stationed.", "https://static.wikia.nocookie.net/starwars/images/8/81/Adelphi.png/revision/latest?cb=20230425072413"),
        PlanetContent("Arcana", "Arcana is a planet that has many structures and fortresses constructed by the Nightsister witches of Dathomir during ancient times. Ahsoka Tano visited Arcana to retrieve a map which led straight to Grand Admiral Thrawn.", "https://static.wikia.nocookie.net/starwars/images/5/54/ArcanaFortress.jpg/revision/latest?cb=20230824144054"),
        PlanetContent("Lothal", "Lothal is a planet located in the Outer Rim Territories and is the home planet of Ezra Bridger. It has a Jedi temple that Ezra and his master Kanan Jarrus visited often and the temple served as a gateway to the World Between Worlds. It was under heavy Imperial rule before it was liberated due to the efforts of Ezra and the Ghost crew. Ahsoka Tano later visited Lothal to take Sabine Wren on a journey to find Ezra Bridger.", "https://static.wikia.nocookie.net/starwars/images/a/ac/Lothal-SM.png/revision/latest?cb=20200909025000"),
        PlanetContent("Corellia", "Corellia is a planet located in the Core Worlds region and is the home planet of Han Solo. The planet has vast shipyards that were used to build starfighters and Star Destroyers for the Empire. The shipyards were later used for redistributing parts for the New Republic but many of its citizen are still loyal the Empire.", "https://static.wikia.nocookie.net/starwars/images/d/d7/Corellia-SWCT.png/revision/latest?cb=20231206040152"),
        PlanetContent("Seatos", "Seatos is a planet located in the Denab system. It is known for its oceans, rocky surfaces, and red forests. It was originally inhabited by the Dathomiri that had left behind a mechanism, which could be powered by a star map, to locate Peridea. Ahsoka, Sabine, and Huyang landed on Seatos and encountered a herd of Purrgil while trying to investigate Morgan Elsbeth's Eye of Sion.", "https://static.wikia.nocookie.net/starwars/images/7/7d/Seatos-PartThree.png/revision/latest?cb=20230903170541"),
        PlanetContent("Peridea", "Peridea is a planet located in the far galaxy. It is the home-world of the Noti and once housed the Witch Kingdom of the Dathomiri. The rings of the planet consist of Purrgil bones, as members of the species come to Peridea to die. Grand Admiral Thrawn and Ezra Bridger were banished to Peridea by a pod of Purrgil following the Liberation of Lothal.", "https://static.wikia.nocookie.net/starwars/images/3/34/Peridea-Ahsoka-PartSix.png/revision/latest?cb=20231125095741"),
        PlanetContent("Dathomir", "Dathomir is a remote planet located in the Dathomir system of the Quelli sector in the Outer Rim Territories. The dark side of the Force has a strong presence on Dathomir and it is home to several witch clans. Grand Admiral Thrawn, his forces, and the Great Mothers escaped to Dathomir from Peridea.", "https://static.wikia.nocookie.net/starwars/images/3/34/DathomirJFO.jpg/revision/latest?cb=20200222032237"),
        )

    transaction {
        Planets.batchInsert(planets){
            (name, description, imgUrl) ->
            this[Planets.name] = name
            this[Planets.description] = description
            this[Planets.imgUrl] = imgUrl
        }
    }
}

val planetsDao = PlanetsDaoFacadeImpl().apply {
    insertPlanets()
}