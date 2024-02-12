package com.api.dao.characters

import com.api.models.*
import com.api.dao.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.random.Random
import kotlin.random.nextInt


class CharactersDaoFacadeImpl : CharactersDaoFacade {
    private fun resultRowToCharacter(row: ResultRow): Character = Character(
        id = row[Characters.id],
        name = row[Characters.name],
        description = row[Characters.description]
    )

    override suspend fun randomCharacter(): Character? {
        return dbQuery {
            val numberOfCharacters = Characters.selectAll().count()
            val randomNumber = Random.nextInt(1..numberOfCharacters.toInt())
            Characters.select {Characters.id eq randomNumber}.map(::resultRowToCharacter).singleOrNull()
        }
    }

    override suspend fun characterByName(characterName: String): Character? {
        return dbQuery {
            Characters.select {Characters.name eq characterName}.map(::resultRowToCharacter).singleOrNull()
        }
    }

    override suspend fun addCharacter(name: String, description: String): Character? {
        return dbQuery {
            val insertStatement = Characters.insert {
                it[this.name] = name
                it[this.description] = description
            }
            insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToCharacter)
        }
    }

    override suspend fun removeCharacter(id: Int): Boolean {
        return dbQuery {
            Characters.deleteWhere {Characters.id eq id}
        } > 0
    }

    override suspend fun editCharacter(id: Int, name: String, description: String): Boolean {
        return dbQuery {
            Characters.update({Characters.id eq id}){
                it[this.name] = name
                it[this.description] = description
            }
        } > 0
    }
}

fun insertCharacters(){
    val characters = listOf(
        CharacterContent("The Mandalorian",  "The Mandalorian, also known as Mando or Din Djarin, is a bounty hunter navigating the outer rim of the galaxy in the era after the fall of the Galactic Empire. He unexpectedly becomes the guardian of Grogu, a young Force-sensitive being. He forms a strong bond with Grogu, despite his initial reluctance and conflicting Mandalorian beliefs."),
        CharacterContent("Grogu",  "Grogu is a young Force-sensitive being that belongs to the same species as Yoda. He was found by The Mandalorian during a bounty hunt on Arvala-7. Grogu forms an unlikely bond with The Mandalorian, becoming his adopted son. Together, they navigate the dangers of the outer rim, facing bounty hunters, Imperial remnants, and other threats."),
        CharacterContent("The Armorer",  "The Armorer is the leader of a hidden Mandalorian tribe called the Children of the Watch. She crafts and repairs beskar armor and embodies the ancient Mandalorian code and upholds strict traditions. She serves as a moral compass, challenging The Mandalorian and pushing him to confront his inner conflicts."),
        CharacterContent("Bo-Katan Kryze",  "Bo-Katan Kryze is a Mandalorian princess. She was a lieutenant in the Death Watch group and leader of the Mandalore resistance during the Clone Wars. She met The Mandalorian while searching for Moff Gideon and the Darksaber. The two eventually teamed up with their respective tribes and retook the planet of Mandalore."),
        CharacterContent("Greef Karga",  "Initially, Greef Karga, was the Guild Master of Bounty Hunters on Nevarro and tried to kill the Mandalorian after he saved Grogu from the Empire. However, after Grogu saved his life, he helped The Mandalorian defeat Moff Gideon and remove the Imperials from Nevarro. Since then he has turned Nevarro into a trade outpost. He provides crucial advice and resources to The Mandalorian and is a staunch defender of Grogu."),
        CharacterContent("Ahsoka Tano",  "Once a young Padawan under Anakin Skywalker, Ahsoka Tano is now a seasoned warrior. She met The Mandalorian and Grogu on Corvus. She guided The Mandalorian towards Grogu's destiny and warned him of the dangers Grogu faces. She teamed up with her apprentice Sabine Wren to find Ezra Bridger and tried to stop the return of Grand Admiral Thrawn."),
        CharacterContent("Boba Fett",  "Once a feared bounty hunter, Boba Fett has carved a new path for himself in the post-Empire galaxy. Boba Fett emerged from the sands of Tatooine with a desire to reclaim his reputation and armor. He forms a strong alliance with Fennec Shand and together they helped the Mandalorian rescue Grogu from Moff Gideon. He is now the Daimyo of Tatooine, seeking respect and control."),
        CharacterContent("Fennec Shand",  "Fennec Shand was once a top-tier mercenary and assassin. She is renowned for her marksmanship and deadly skills. After being saved by Boba Fett, Fennec forms a formidable partnership, serving as his trusted advisor and lieutenant. Fennec develops a loyalty to Boba Fett and his vision for Tatooine's future."),
        CharacterContent("Cara Dune",  "Cara Dune is an Alderaanian and an experienced soldier. She used to be a shock trooper in the Alliance to Restore the Republic and was also the marshal of Nevarro for a brief period. She assisted The Mandalorian in rescuing Grogu from Moff Gideon before being recruited by the New Republic Special Forces."),
        CharacterContent("Dr. Pershing",  "Dr. Penn Pershing initially worked for the remnants of the Galatic Empire under Moff Gideon. He is an expert in cloning and genetic manipulation. He possesses a deep scientific curiosity but shows willingness to employ unethical methods for Imperial ambitions. He did express discomfort with harming Grogu while performing experiments. He was later captured by the New Republic and entered their amnesty program."),
        CharacterContent("Moff Gideon",  "Moff Gideon was cunning and ruthless leader of an Imperial remnant. He was obsessed with capturing Grogu as he believed his blood held the key to creating powerful clones. He was driven by a desire for control and personal gain. He eventually met his fate while fighting the Mandalorian resistance on Mandalore."),
        CharacterContent("Thrawn",  "Grand Admiral Thrawn belongs to the Chiss species, know for their analytical minds and blue skin with red eyes. He possess an unmatched intellect and strategic prowess, allowing him to predict and outmaneuver his opponents. He emerges from exile on Peridea, assembling a new force with the help of Morgan Elsbeth. He returns to Dathomir to reunify the Imperial Remnant."),
        CharacterContent("Kuiil",  "Kuiil was an Ugnaught male who lived on Arvala-7. He worked as a vapor farmer, sought peace and possessed extraordinary technical skills. He assisted The Mandalorian repair his ship after it was looted by Jawas. He eventually met his fate by scout troopers while watching over Grogu on Nevarro."),
        CharacterContent("IG-11",  "IG-11 was initially a bounty hunter droid, known for its mercilessness and advanced killing capabilities. He was later reprogrammed by Kuiil in order to assist him at his moisture farm. IG-11 later sacrificed himself via self-destruction in order to kill a platoon of stormtroopers to prevent Grogu from being captured. He was later reconstructed and given the position of marshal of Nevarro."),
        CharacterContent("Peli Motto",  "Peli Motto is a vibrant and resourceful mechanic who manages Hangar 3-5 at Mos Eisley spaceport on Tatooine. She is renowned for her ability to repair and refuel starships. She is often surrounded by ismantled parts and tools, showcasing her love for tinkering and problem-solving. She develops a friendship with The Mandalorian and Grogu, offering them shelter, repairs, and even moral support."),
        CharacterContent("Baylan Skoll",  "Baylan Skoll is a former Jedi Knight who managed to survive the Great Jedi Purge. He is driven by a passionate belief that the Jedi failed the galaxy, he seeks to destroy the existing order and rebuild it in a new form. Him and his apprentice, Shin Hati, were allied with forces of Morgan Elsbeth and together they aided the return of Grand Admiral Thrawn. After Thrawn's return, Skoll continued his search for his destiny on Peridea."),
        CharacterContent("Morgan Elsbeth",  "Morgan Elsbeth was a Nightsister and an industrialist. She was the cruel and tyrannical magistrate of Calodan on the planet Corvus. She was defined by ambition, ruthlessness and a surprising connection to the Force. She retrieved a star map which led her directly to Grand Admiral Thrawn on Peridea. Ultimately, she was defeated by Ahsoka on Peridea."),
        CharacterContent("Huyang",  "Huyang is a Mark IV architect droid as has served the Jedi Order for centuries as a lightsaber architect. He has guided younglings through the process of constructing their personal blades. His expertise extends beyond technical know-how, embodying the Jedi philosophy. He assisted Ahsoka and Sabine Wren on their adventure to find Ezra Bridger."),
        CharacterContent("Paz Vizsla",  "Paz Vizsla was a fierce Mandalorian warrior, loyal to his clan and customs. He was member of the Children of the Watch and initially had clashes with the Mandalorian. After The Mandalorian and Bo-Katan rescued his son, he agreed to help them free Nevarro from pirates. He was eventually killed in action while holding the line against Moff Gideon's armored commandos and Praetorian Guards."),
        CharacterContent("Carson Teva",  "Captain Carson Teva is a dedicated captain within the Adelphi Rangers of the New Republic Starfighter Corps. Carson Teva is fiercely protective of the fragile peace following the Empire's fall. He was initially wary of The Mandalorian but later provides advice and assistance. The Mandalorian later offers his help to Carson to hunt down Imperial remnants."),
        CharacterContent("Cobb Vanth",  "Cobb Vanth is the sheriff and mayor of Mos Pelgo, which was eventually renamed Freetown. He met The Mandalorian who came to Mos Pelgo searching for a fellow Mandalorian. At that time, Vanth was wearing Boba Fett's armor and agreed to give it to The Mandalorian if he assisted him in defeating a krayt dragon. Later, when the Pyke Syndicate were attempting to takeover Tatooine, Vanth was shot by Cad Bane. Once the Pyke Syndicate were defeated, Cobb Vanth started receiving treatment."),
        CharacterContent("Mayfeld",  "Migs Mayfeld is mercenary and former sharpshooter of the Imperial Army. He was recruited by Ranzar Malk, to work in team which included The Mandalorian, to rescue a prisoner. During the mission he had betrayed Din Djarin but then was later defeated by him. Later, The Mandalorian required his assistance in finding Moff Gideon's ship and was subsequently set free by Cara Dune."),
        CharacterContent("Shin Hati",  "Shin Hati is the apprentice of Baylan Skoll and together they worked with Morgan Elsbeth to find Grand Admiral Thrawn. Hati eventually questioned her master and their paths went separate ways. In the end, she was left stranded on Peridea."),
        CharacterContent("Sabine Wren",  "Sabine Wren is a female Mandalorian warrior and has the ability to use the Force. She was initially recruited by Hera Syndulla and Kanan Jarrus to join their rebel crew and was able to help them liberate the planet of Lothal from the Imperials. She later became the apprentice of Ahsoka Tano but Ahsoka parted ways with her. Ahsoka eventually returned to her and together they went on a journey to find Ezra Bridger and stop the return of Thrawn. In the end, both were left stranded on Peridea." ),
        CharacterContent("Luke Skywalker",  "Luke Skywalker is a legendary Jedi Master who fought against the Empire alongside Princess Leia Organa and Han Solo. He later rescued Grogu from Moff Gideon's forces and temporarily took him as his apprentice on Ossus. Luke eventually offered Grogu of which Grogu chose to return to The Mandalorian."),
        CharacterContent("Anakin Skywalker",  "Anakin Skywalker was a legendary Jedi Knight and eventually became Darth Vader. During the Clone Wars, he oversaw the training of his apprentice, Ahsoka Tano. They both met again in the World Between Worlds, a mystical dimension within the Force that touches every point of time and space, where Anakin had one final lesson to teach Ahsoka."),
        CharacterContent("Ezra Bridger",  "Ezra Bridger is a Bokken Jedi who was taught the ways of the Force by Kanan Jarrus. He managed to liberate his home world of Lothal from Imperial control but him and Grand Admiral Thrawn were taken into hyperspace by the purrgil. He was eventually found Sabine Wren and Ahsoka on Peridea but left them behind while he escaped on Thrawn's Star Destroyer."),
        CharacterContent("Hera Syndulla",  "Hera Syndulla is a strong female leader and was a member of the Ghost crew. She continued to help Ahsoka in her hunt for Thrawn and continued to warn the New Republic of the dangers of Thrawn's return."),
        CharacterContent("Great Mothers",  "The Great Mothers are the three Dathomirian Nightsisters who allied with Grand Admiral Thrawn. The are particular skilled in reading threads of fate and destiny. Their names are Klothow, Aktropaw and Lakesis"),
        CharacterContent("Cad Bane",  "Cad Bane was a Duros male bounty hunter. He was infamous during the Clone Wars, known for fighting Jedis such as Obi-Wan Kenobu and Anakin Skywalker. He was later hired by the Pyke Syndicate to assist them to takeover Tatooine but was eventually killed by Boba Fett."),
        CharacterContent("Axe Woves",  "Axe Woves is a male Mandalorian who worked alongside Koska Reeves and Bo-Katan Kryze. The three of them plus The Mandalorian were able to steal an Imperial cruiser on Trask. Later, Woves was part of the resistance to reclaim the planet of Mandalore."),
        CharacterContent("Elia Kane",  "Elia Kane is a comms officer that worked for Moff Gideon. She was captured by the New Republic and placed in their Amnesty Program. However, she remained devoted to the cause of the Empire, and secretly continued to take directions from Moff Gideon."),
        CharacterContent("Koska Reeves",  "Koska Reeves is a female Mandalorian who helped steal the Imperial cruiser alongside Bo-Katan Kryze, Axe Woves and Din Djarin on Trask. Later, she was part of the Mandalorian resistance to reclaim the planet of Mandalore."),
        CharacterContent("Mok Shaiz's Majordomo",  "Mok Shaiz's Majordomo is the majordomo of the mayor of Mos Espa, Mok Shaiz. He continued to lie to Boba Fett and Fennec Shand about the mayor to protect the mayor's secrecy but was eventually captured by the Daimyo."),
        CharacterContent("The Client",  "The Client was part of the Imperial remnant led by Moff Gideon. He operated from a safe house in Nevarro alongside Dr. Pershing and had hired The Mandalorian to find Grogu. He was ordered to extract blood from Grogu for Gideon's cloning project. He was eventually killed by Moff Gideon's men."),
        CharacterContent("Frog Lady",  "The Frog Lady was assisted by The Mandalorian who escorted her to Trask as she had eggs that needed to be fertilized by her husband. She has strong technical skills as she was able to repair a droid so that she could communicate with The Mandalorian while they had crashed on Maldo Kreis."),
        CharacterContent("Hutt Twins",  "The Hutt Twins are the cousins of Jabba the Hutt and had sent Krrsantan to kill Boba Fett as they had laid claim on Jabba's territory. They had apologised to Boba Fett after Krrsantan's failure and had gifted him a rancor."),
        CharacterContent("Garsa Fwip",  "Garsa Fwip was the owner of the Sanctuary, a cantina on Mos Espa. She was ensured the longevity of her business by Boba Fett. She was eventually killed after the Sanctuary was bombed by the Pyke Syndicate."),
        CharacterContent("Jacen Syndulla",  ""),
        CharacterContent("Mon Mothma",  ""),
        CharacterContent("Marrok",  ""),
        CharacterContent("Enoch",  ""),
        CharacterContent("Toro Calican",  ""),
        CharacterContent("Ranzar Malk",  ""),
        CharacterContent("Xi'an",  ""),
        CharacterContent("Bib Fortuna",  ""),
        CharacterContent("8D8",  ""),
        CharacterContent("Mayor Mok Shaiz",  ""),
        CharacterContent("Skad",  ""),
        CharacterContent("Drash",  ""),
        CharacterContent("Rancor Keeper",  ""),
        CharacterContent("Krrsantan",  ""),
        CharacterContent("Pyke Boss",  ""),
        CharacterContent("The Anzellan",  ""),
        CharacterContent("Gorian Shard",  ""),
        CharacterContent("Captain Bombardier",  ""),
        CharacterContent("The Duchess",  ""),
        )

    transaction {
        Characters.batchInsert(characters){
            (name, description) ->
            this[Characters.name] = name
            this[Characters.description] = description
        }
    }
}

val charactersDAO = CharactersDaoFacadeImpl().apply {
   insertCharacters()
}