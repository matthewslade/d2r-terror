package com.d2rterror.data.local

import com.d2rterror.data.model.Element
import com.d2rterror.data.model.Element.*
import com.d2rterror.data.model.TerrorZone
import com.d2rterror.data.model.TerrorZoneGroup
import com.d2rterror.data.model.Tier

/**
 * Terror Zone Groups for Diablo 2 Resurrected.
 *
 * Each group contains individual zones with their own immunities (Hell difficulty).
 * Tier list sourced from maxroll.gg terror zone tier list.
 */
object ZoneData {

    private val terrorZoneGroups: List<TerrorZoneGroup> = listOf(
        // ── Act I ──
        // Tiers sourced from maxroll.gg terror zone tier list
        TerrorZoneGroup(1, "Blood Moor and Den of Evil", 1,
            listOf("blood moor", "den of evil"),
            zones = listOf(
                TerrorZone("Blood Moor", setOf(FIRE, COLD), Tier.F),
                TerrorZone("Den of Evil", setOf(FIRE, COLD), Tier.F)
            )),
        TerrorZoneGroup(2, "Cold Plains and The Cave", 1,
            listOf("cold plains", "cave"),
            zones = listOf(
                TerrorZone("Cold Plains", setOf(FIRE, COLD, LIGHTNING), Tier.A),
                TerrorZone("The Cave", setOf(FIRE, COLD, LIGHTNING), Tier.A)
            )),
        TerrorZoneGroup(3, "Burial Grounds, The Crypt, and the Mausoleum", 1,
            listOf("burial grounds", "crypt", "mausoleum"),
            zones = listOf(
                TerrorZone("Burial Grounds", setOf(LIGHTNING), Tier.D),
                TerrorZone("The Crypt", setOf(LIGHTNING), Tier.D),
                TerrorZone("The Mausoleum", setOf(LIGHTNING), Tier.D)
            )),
        TerrorZoneGroup(4, "Stony Field", 1,
            listOf("stony field"),
            zones = listOf(
                TerrorZone("Stony Field", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.C)
            )),
        TerrorZoneGroup(5, "Dark Wood and Underground Passage", 1,
            listOf("dark wood", "underground passage"),
            zones = listOf(
                TerrorZone("Dark Wood", setOf(FIRE, COLD, POISON), Tier.B),
                TerrorZone("Underground Passage", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.B)
            )),
        TerrorZoneGroup(6, "Black Marsh and The Hole", 1,
            listOf("black marsh", "hole"),
            zones = listOf(
                TerrorZone("Black Marsh", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.S),
                TerrorZone("The Hole", setOf(FIRE, COLD, LIGHTNING), Tier.S)
            )),
        TerrorZoneGroup(7, "The Forgotten Tower", 1,
            listOf("forgotten tower", "tower cellar"),
            zones = listOf(
                TerrorZone("The Forgotten Tower", setOf(FIRE, LIGHTNING, PHYSICAL), Tier.S),
                TerrorZone("Tower Cellar", setOf(FIRE, COLD, LIGHTNING, PHYSICAL), Tier.S, hasKeyDrop = true)
            )),
        TerrorZoneGroup(8, "Jail and Barracks", 1,
            listOf("jail", "barracks"),
            zones = listOf(
                TerrorZone("Jail", setOf(FIRE, COLD, POISON), Tier.A),
                TerrorZone("Barracks", setOf(FIRE, COLD, POISON), Tier.A)
            )),
        TerrorZoneGroup(9, "Cathedral and Catacombs", 1,
            listOf("cathedral", "catacombs", "inner cloister"),
            zones = listOf(
                TerrorZone("Cathedral", setOf(FIRE, COLD, LIGHTNING), Tier.S),
                TerrorZone("Inner Cloister", setOf(FIRE, COLD), Tier.S),
                TerrorZone("Catacombs", setOf(FIRE, COLD, LIGHTNING, POISON, PHYSICAL), Tier.S, hasBoss = true)
            )),
        TerrorZoneGroup(10, "The Pit", 1,
            listOf("pit", "tamoe highland", "outer cloister"),
            zones = listOf(
                TerrorZone("Outer Cloister", setOf(FIRE, COLD), Tier.S),
                TerrorZone("Tamoe Highland", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.S),
                TerrorZone("The Pit", setOf(FIRE, COLD, LIGHTNING), Tier.S)
            )),
        TerrorZoneGroup(11, "Tristram", 1,
            listOf("tristram"),
            zones = listOf(
                TerrorZone("Tristram", setOf(FIRE, LIGHTNING, POISON), Tier.C)
            )),
        TerrorZoneGroup(12, "Moo Moo Farm", 1,
            listOf("moo moo farm", "secret cow level", "cow level"),
            zones = listOf(
                TerrorZone("Moo Moo Farm", setOf(LIGHTNING), Tier.C)
            )),

        // ── Act II ──
        TerrorZoneGroup(13, "Sewers", 2,
            listOf("sewers", "sewer"),
            zones = listOf(
                TerrorZone("Sewers", setOf(FIRE, COLD, POISON, MAGIC), Tier.A)
            )),
        TerrorZoneGroup(14, "Rocky Waste and Stony Tomb", 2,
            listOf("rocky waste", "stony tomb"),
            zones = listOf(
                TerrorZone("Rocky Waste", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.S),
                TerrorZone("Stony Tomb", setOf(FIRE, COLD, LIGHTNING, POISON, MAGIC), Tier.S)
            )),
        TerrorZoneGroup(15, "Dry Hills and Halls of the Dead", 2,
            listOf("dry hills", "halls of the dead"),
            zones = listOf(
                TerrorZone("Dry Hills", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.A),
                TerrorZone("Halls of the Dead", setOf(FIRE, COLD, LIGHTNING, POISON, PHYSICAL), Tier.A)
            )),
        TerrorZoneGroup(16, "Far Oasis", 2,
            listOf("far oasis", "maggot lair"),
            zones = listOf(
                TerrorZone("Far Oasis", setOf(FIRE, POISON, PHYSICAL), Tier.D),
                TerrorZone("Maggot Lair", setOf(FIRE, LIGHTNING, POISON), Tier.D)
            )),
        TerrorZoneGroup(17, "Lost City, Valley of Snakes, and Claw Viper Temple", 2,
            listOf("lost city", "valley of snakes", "claw viper"),
            zones = listOf(
                TerrorZone("Lost City", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.A),
                TerrorZone("Valley of Snakes", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.A),
                TerrorZone("Claw Viper Temple", setOf(FIRE, COLD, LIGHTNING, MAGIC), Tier.A)
            )),
        TerrorZoneGroup(18, "Ancient Tunnels", 2,
            listOf("ancient tunnels"),
            zones = listOf(
                TerrorZone("Ancient Tunnels", setOf(FIRE, LIGHTNING, POISON), Tier.A)
            )),
        TerrorZoneGroup(19, "Arcane Sanctuary", 2,
            listOf("arcane sanctuary", "palace cellar", "harem"),
            zones = listOf(
                TerrorZone("Harem", setOf(FIRE, COLD, LIGHTNING), Tier.C),
                TerrorZone("Palace Cellar", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.C),
                TerrorZone("Arcane Sanctuary", setOf(FIRE, COLD, LIGHTNING, POISON, PHYSICAL), Tier.C, hasKeyDrop = true)
            )),
        TerrorZoneGroup(20, "Tal Rasha's Tombs", 2,
            listOf("tal rasha", "tomb", "canyon of the magi"),
            zones = listOf(
                TerrorZone("Canyon of the Magi", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.S),
                TerrorZone("Tal Rasha's Tombs", setOf(FIRE, COLD, LIGHTNING, POISON, MAGIC), Tier.S, hasBoss = true)
            )),

        // ── Act III ──
        TerrorZoneGroup(21, "Spider Forest and Spider Cavern", 3,
            listOf("spider forest", "spider cavern", "arachnid lair"),
            zones = listOf(
                TerrorZone("Spider Forest", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.A),
                TerrorZone("Spider Cavern", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.A),
                TerrorZone("Arachnid Lair", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.A)
            )),
        TerrorZoneGroup(22, "Great Marsh", 3,
            listOf("great marsh"),
            zones = listOf(
                TerrorZone("Great Marsh", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.B)
            )),
        TerrorZoneGroup(23, "Flayer Jungle and Flayer Dungeon", 3,
            listOf("flayer jungle", "flayer dungeon", "swampy pit"),
            zones = listOf(
                TerrorZone("Flayer Jungle", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.S),
                TerrorZone("Flayer Dungeon", setOf(FIRE, COLD, LIGHTNING, POISON, MAGIC), Tier.S),
                TerrorZone("Swampy Pit", setOf(FIRE, COLD, LIGHTNING, POISON, PHYSICAL), Tier.S)
            )),
        TerrorZoneGroup(24, "Kurast Bazaar, Ruined Temple, and Disused Fane", 3,
            listOf("kurast bazaar", "ruined temple", "disused fane", "upper kurast",
                "lower kurast", "kurast causeway", "forgotten temple", "forgotten reliquary",
                "disused reliquary", "ruined fane"),
            zones = listOf(
                TerrorZone("Lower Kurast", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.S),
                TerrorZone("Kurast Bazaar", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.S),
                TerrorZone("Upper Kurast", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.S),
                TerrorZone("Kurast Causeway", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.S),
                TerrorZone("Ruined Temple", setOf(FIRE, COLD, LIGHTNING, POISON, PHYSICAL, MAGIC), Tier.S),
                TerrorZone("Disused Fane", setOf(FIRE, COLD, LIGHTNING, POISON, PHYSICAL, MAGIC), Tier.S),
                TerrorZone("Forgotten Temple", setOf(FIRE, COLD, LIGHTNING, POISON, PHYSICAL, MAGIC), Tier.S),
                TerrorZone("Forgotten Reliquary", setOf(FIRE, COLD, LIGHTNING, POISON, PHYSICAL, MAGIC), Tier.S),
                TerrorZone("Disused Reliquary", setOf(FIRE, COLD, LIGHTNING, POISON, PHYSICAL, MAGIC), Tier.S),
                TerrorZone("Ruined Fane", setOf(FIRE, COLD, LIGHTNING, POISON, PHYSICAL, MAGIC), Tier.S)
            )),
        TerrorZoneGroup(25, "Travincal", 3,
            listOf("travincal"),
            zones = listOf(
                TerrorZone("Travincal", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.C)
            )),
        TerrorZoneGroup(26, "Durance of Hate", 3,
            listOf("durance of hate"),
            zones = listOf(
                TerrorZone("Durance of Hate", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.D, hasBoss = true)
            )),

        // ── Act IV ──
        TerrorZoneGroup(27, "Outer Steppes and Plains of Despair", 4,
            listOf("outer steppes", "plains of despair"),
            zones = listOf(
                TerrorZone("Outer Steppes", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.A),
                TerrorZone("Plains of Despair", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.A)
            )),
        TerrorZoneGroup(28, "River of Flame and City of the Damned", 4,
            listOf("river of flame", "city of the damned"),
            zones = listOf(
                TerrorZone("River of Flame", setOf(FIRE, COLD, LIGHTNING, PHYSICAL), Tier.A),
                TerrorZone("City of the Damned", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.A)
            )),
        TerrorZoneGroup(29, "Chaos Sanctuary", 4,
            listOf("chaos sanctuary"),
            zones = listOf(
                TerrorZone("Chaos Sanctuary", setOf(FIRE, COLD, LIGHTNING), Tier.S, hasBoss = true)
            )),

        // ── Act V ──
        TerrorZoneGroup(30, "Bloody Foothills, Frigid Highlands, and Abaddon", 5,
            listOf("bloody foothills", "frigid highlands", "abaddon"),
            zones = listOf(
                TerrorZone("Bloody Foothills", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.B),
                TerrorZone("Frigid Highlands", setOf(FIRE, COLD, LIGHTNING, POISON, PHYSICAL, MAGIC), Tier.B),
                TerrorZone("Abaddon", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.B)
            )),
        TerrorZoneGroup(31, "Glacial Trail and Drifter Cavern", 5,
            listOf("glacial trail", "drifter cavern"),
            zones = listOf(
                TerrorZone("Glacial Trail", setOf(FIRE, COLD, LIGHTNING, POISON, PHYSICAL), Tier.A),
                TerrorZone("Drifter Cavern", setOf(COLD, LIGHTNING), Tier.A)
            )),
        TerrorZoneGroup(32, "Crystalline Passage and Frozen River", 5,
            listOf("crystalline passage", "frozen river"),
            zones = listOf(
                TerrorZone("Crystalline Passage", setOf(FIRE, COLD, LIGHTNING, POISON, PHYSICAL), Tier.A),
                TerrorZone("Frozen River", setOf(COLD, LIGHTNING, PHYSICAL, MAGIC), Tier.A)
            )),
        TerrorZoneGroup(33, "Arreat Plateau and Pit of Acheron", 5,
            listOf("arreat plateau", "pit of acheron", "frozen tundra", "infernal pit"),
            zones = listOf(
                TerrorZone("Arreat Plateau", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.C),
                TerrorZone("Frozen Tundra", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.C),
                TerrorZone("Pit of Acheron", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.C),
                TerrorZone("Infernal Pit", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.C)
            )),
        TerrorZoneGroup(34, "Nihlathak's Temple and Halls", 5,
            listOf("nihlathak", "halls of anguish", "halls of pain", "halls of vaught"),
            zones = listOf(
                TerrorZone("Nihlathak's Temple", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.A),
                TerrorZone("Halls of Anguish", setOf(FIRE, COLD, LIGHTNING, POISON), Tier.A),
                TerrorZone("Halls of Pain", setOf(FIRE, COLD, LIGHTNING, POISON, PHYSICAL), Tier.A),
                TerrorZone("Halls of Vaught", setOf(FIRE, COLD, LIGHTNING, POISON, PHYSICAL, MAGIC), Tier.A, hasKeyDrop = true)
            )),
        TerrorZoneGroup(35, "Ancient's Way and Icy Cellar", 5,
            listOf("ancient's way", "ancients way", "icy cellar"),
            zones = listOf(
                TerrorZone("Ancient's Way", setOf(COLD, LIGHTNING, POISON, PHYSICAL), Tier.B),
                TerrorZone("Icy Cellar", setOf(COLD, LIGHTNING), Tier.B)
            )),
        TerrorZoneGroup(37, "Kurast Sewers", 3,
            listOf("kurast sewers"),
            zones = listOf(
                TerrorZone("Kurast Sewers", setOf(COLD, LIGHTNING, POISON, MAGIC), Tier.B)
            )),
        TerrorZoneGroup(36, "Worldstone Keep, Throne of Destruction, and Worldstone Chamber", 5,
            listOf("worldstone keep", "throne of destruction", "worldstone chamber"),
            zones = listOf(
                TerrorZone("Worldstone Keep", setOf(FIRE, COLD, LIGHTNING, POISON, PHYSICAL), Tier.S),
                TerrorZone("Throne of Destruction", setOf(FIRE, COLD, LIGHTNING, POISON, PHYSICAL, MAGIC), Tier.S, hasBoss = true),
                TerrorZone("Worldstone Chamber", setOf(FIRE, COLD, LIGHTNING, POISON, PHYSICAL), Tier.S)
            ))
    )

    fun findZoneByScrapedName(scrapedName: String): Int? {
        val normalizedName = scrapedName.lowercase().trim()
        android.util.Log.d("ZoneData", "Finding zone for scraped name: '$scrapedName' -> normalized: '$normalizedName'")

        // Collect all candidate matches with the keyword that matched
        var bestMatch: TerrorZoneGroup? = null
        var bestKeyword = ""

        for (zone in terrorZoneGroups) {
            for (keyword in zone.keywords) {
                val matches = normalizedName == keyword ||
                        normalizedName.contains(keyword) ||
                        keyword.contains(normalizedName)
                // Prefer the longest matching keyword (most specific match)
                if (matches && keyword.length > bestKeyword.length) {
                    bestMatch = zone
                    bestKeyword = keyword
                }
            }
        }

        if (bestMatch != null) {
            android.util.Log.d("ZoneData", "MATCHED: '$normalizedName' -> Zone ${bestMatch.id} '${bestMatch.name}' via keyword '$bestKeyword'")
            return bestMatch.id
        }

        android.util.Log.w("ZoneData", "NO MATCH for: '$normalizedName'")
        return null
    }

    fun getZoneById(id: Int): TerrorZoneGroup? = terrorZoneGroups.find { it.id == id }

    fun getAllZones(): List<TerrorZoneGroup> = terrorZoneGroups
        .sortedWith(compareBy({ it.act }, { it.name }))

    fun getZonesByAct(): Map<Int, List<TerrorZoneGroup>> = terrorZoneGroups
        .groupBy { it.act }
        .mapValues { (_, zones) -> zones.sortedBy { it.name } }

    fun getZonesForAct(act: Int): List<TerrorZoneGroup> =
        terrorZoneGroups
            .filter { it.act == act }
            .sortedBy { it.name }

    fun getZonesByIds(ids: List<Int>): List<TerrorZoneGroup> =
        ids.mapNotNull { id -> terrorZoneGroups.find { it.id == id } }

    fun getAllZoneIds(): Set<Int> = terrorZoneGroups.map { it.id }.toSet()
}
