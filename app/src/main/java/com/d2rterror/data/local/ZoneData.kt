package com.d2rterror.data.local

import com.d2rterror.data.model.Element
import com.d2rterror.data.model.Element.*
import com.d2rterror.data.model.TerrorZone
import com.d2rterror.data.model.Tier

/**
 * Terror Zone Groups for Diablo 2 Resurrected.
 *
 * Immunities sourced from maxroll.gg (Hell difficulty).
 * Tier list sourced from maxroll.gg terror zone tier list.
 * Immunities are the union of all sub-areas in the group.
 */
object ZoneData {

    private val terrorZoneGroups: List<TerrorZone> = listOf(
        // Act I
        TerrorZone(1, "Blood Moor and Den of Evil", 1,
            listOf("blood moor", "den of evil"),
            immunities = setOf(FIRE, COLD),
            tier = Tier.F),
        TerrorZone(2, "Cold Plains and The Cave", 1,
            listOf("cold plains", "cave"),
            immunities = setOf(FIRE, COLD, LIGHTNING),
            tier = Tier.A),
        TerrorZone(3, "Burial Grounds, The Crypt, and the Mausoleum", 1,
            listOf("burial grounds", "crypt", "mausoleum"),
            immunities = setOf(LIGHTNING),
            tier = Tier.D),
        TerrorZone(4, "Stony Field", 1,
            listOf("stony field"),
            immunities = setOf(FIRE, COLD, LIGHTNING, POISON),
            tier = Tier.C),
        TerrorZone(5, "Dark Wood and Underground Passage", 1,
            listOf("dark wood", "underground passage"),
            immunities = setOf(FIRE, COLD, LIGHTNING, POISON),
            tier = Tier.B),
        TerrorZone(6, "Black Marsh and The Hole", 1,
            listOf("black marsh", "hole"),
            immunities = setOf(FIRE, COLD, LIGHTNING, POISON),
            tier = Tier.S),
        TerrorZone(7, "The Forgotten Tower", 1,
            listOf("forgotten tower", "tower cellar"),
            immunities = setOf(FIRE, COLD, LIGHTNING, PHYSICAL),
            tier = Tier.S,
            hasKeyDrop = true),
        TerrorZone(8, "Jail and Barracks", 1,
            listOf("jail", "barracks"),
            immunities = setOf(FIRE, COLD, LIGHTNING),
            tier = Tier.A),
        TerrorZone(9, "Cathedral and Catacombs", 1,
            listOf("cathedral", "catacombs", "inner cloister"),
            immunities = setOf(FIRE, COLD, LIGHTNING),
            tier = Tier.S),
        TerrorZone(10, "The Pit", 1,
            listOf("pit", "tamoe highland"),
            immunities = setOf(FIRE, COLD, LIGHTNING, POISON),
            tier = Tier.S),
        TerrorZone(11, "Tristram", 1,
            listOf("tristram"),
            immunities = setOf(FIRE, LIGHTNING, POISON),
            tier = Tier.C),
        TerrorZone(12, "Moo Moo Farm", 1,
            listOf("moo moo farm", "secret cow level", "cow level"),
            immunities = setOf(LIGHTNING),
            tier = Tier.C),

        // Act II
        TerrorZone(13, "Sewers", 2,
            listOf("sewers", "sewer"),
            immunities = setOf(FIRE, COLD, LIGHTNING, POISON),
            tier = Tier.A),
        TerrorZone(14, "Rocky Waste and Stony Tomb", 2,
            listOf("rocky waste", "stony tomb"),
            immunities = setOf(FIRE, COLD, LIGHTNING, POISON),
            tier = Tier.S),
        TerrorZone(15, "Dry Hills and Halls of the Dead", 2,
            listOf("dry hills", "halls of the dead"),
            immunities = setOf(FIRE, COLD, LIGHTNING, POISON, PHYSICAL),
            tier = Tier.A),
        TerrorZone(16, "Far Oasis", 2,
            listOf("far oasis"),
            immunities = setOf(FIRE, COLD, LIGHTNING, POISON),
            tier = Tier.D),
        TerrorZone(17, "Lost City, Valley of Snakes, and Claw Viper Temple", 2,
            listOf("lost city", "valley of snakes", "claw viper"),
            immunities = setOf(FIRE, COLD, LIGHTNING, POISON),
            tier = Tier.A),
        TerrorZone(18, "Ancient Tunnels", 2,
            listOf("ancient tunnels"),
            immunities = setOf(FIRE, LIGHTNING, POISON),
            tier = Tier.A),
        TerrorZone(19, "Arcane Sanctuary", 2,
            listOf("arcane sanctuary"),
            immunities = setOf(FIRE, COLD, LIGHTNING),
            tier = Tier.C,
            hasKeyDrop = true),
        TerrorZone(20, "Tal Rasha's Tombs", 2,
            listOf("tal rasha", "tomb"),
            immunities = setOf(FIRE, COLD, LIGHTNING, POISON, PHYSICAL),
            tier = Tier.S),

        // Act III
        TerrorZone(21, "Spider Forest and Spider Cavern", 3,
            listOf("spider forest", "spider cavern", "arachnid lair"),
            immunities = setOf(FIRE, COLD, LIGHTNING, POISON),
            tier = Tier.A),
        TerrorZone(22, "Great Marsh", 3,
            listOf("great marsh"),
            immunities = setOf(FIRE, COLD, LIGHTNING, POISON),
            tier = Tier.B),
        TerrorZone(23, "Flayer Jungle and Flayer Dungeon", 3,
            listOf("flayer jungle", "flayer dungeon", "swampy pit"),
            immunities = setOf(FIRE, COLD, LIGHTNING, POISON),
            tier = Tier.S),
        TerrorZone(24, "Kurast Bazaar, Ruined Temple, and Disused Fane", 3,
            listOf("kurast bazaar", "ruined temple", "disused fane"),
            immunities = setOf(FIRE, COLD, LIGHTNING, POISON),
            tier = Tier.S),
        TerrorZone(25, "Travincal", 3,
            listOf("travincal"),
            immunities = setOf(FIRE, COLD, LIGHTNING),
            tier = Tier.C),
        TerrorZone(26, "Durance of Hate", 3,
            listOf("durance of hate"),
            immunities = setOf(FIRE, COLD, LIGHTNING),
            tier = Tier.D),

        // Act IV
        TerrorZone(27, "Outer Steppes and Plains of Despair", 4,
            listOf("outer steppes", "plains of despair"),
            immunities = setOf(FIRE, COLD, LIGHTNING, POISON, PHYSICAL),
            tier = Tier.A),
        TerrorZone(28, "River of Flame and City of the Damned", 4,
            listOf("river of flame", "city of the damned"),
            immunities = setOf(FIRE, COLD, LIGHTNING, PHYSICAL),
            tier = Tier.A),
        TerrorZone(29, "Chaos Sanctuary", 4,
            listOf("chaos sanctuary"),
            immunities = setOf(FIRE, COLD, LIGHTNING, POISON, PHYSICAL),
            tier = Tier.S),

        // Act V
        TerrorZone(30, "Bloody Foothills, Frigid Highlands, and Abaddon", 5,
            listOf("bloody foothills", "frigid highlands", "abaddon"),
            immunities = setOf(FIRE, COLD, LIGHTNING),
            tier = Tier.B),
        TerrorZone(31, "Glacial Trail and Drifter Cavern", 5,
            listOf("glacial trail", "drifter cavern"),
            immunities = setOf(COLD, LIGHTNING),
            tier = Tier.A),
        TerrorZone(32, "Crystalline Passage and Frozen River", 5,
            listOf("crystalline passage", "frozen river"),
            immunities = setOf(COLD, LIGHTNING),
            tier = Tier.A),
        TerrorZone(33, "Arreat Plateau and Pit of Acheron", 5,
            listOf("arreat plateau", "pit of acheron"),
            immunities = setOf(FIRE, COLD, LIGHTNING, PHYSICAL),
            tier = Tier.C),
        TerrorZone(34, "Nihlathak's Temple and Halls", 5,
            listOf("nihlathak", "halls of anguish", "halls of pain", "halls of vaught"),
            immunities = setOf(FIRE, COLD, LIGHTNING, POISON),
            tier = Tier.A,
            hasKeyDrop = true),
        TerrorZone(35, "Ancient's Way and Icy Cellar", 5,
            listOf("ancient's way", "ancients way", "icy cellar"),
            immunities = setOf(FIRE, COLD, LIGHTNING),
            tier = Tier.B),
        TerrorZone(36, "Worldstone Keep, Throne of Destruction, and Worldstone Chamber", 5,
            listOf("worldstone keep", "throne of destruction", "worldstone chamber"),
            immunities = setOf(FIRE, COLD, LIGHTNING, POISON, PHYSICAL),
            tier = Tier.S)
    )

    fun findZoneByScrapedName(scrapedName: String): Int? {
        val normalizedName = scrapedName.lowercase().trim()
        android.util.Log.d("ZoneData", "Finding zone for scraped name: '$scrapedName' -> normalized: '$normalizedName'")

        for (zone in terrorZoneGroups) {
            for (keyword in zone.keywords) {
                if (normalizedName == keyword || normalizedName.contains(keyword) || keyword.contains(normalizedName)) {
                    android.util.Log.d("ZoneData", "MATCHED: '$normalizedName' -> Zone ${zone.id} '${zone.name}' via keyword '$keyword'")
                    return zone.id
                }
            }
        }

        android.util.Log.w("ZoneData", "NO MATCH for: '$normalizedName'")
        return null
    }

    fun getZoneById(id: Int): TerrorZone? = terrorZoneGroups.find { it.id == id }

    fun getAllZones(): List<TerrorZone> = terrorZoneGroups
        .sortedWith(compareBy({ it.act }, { it.name }))

    fun getZonesByAct(): Map<Int, List<TerrorZone>> = terrorZoneGroups
        .groupBy { it.act }
        .mapValues { (_, zones) -> zones.sortedBy { it.name } }

    fun getZonesForAct(act: Int): List<TerrorZone> =
        terrorZoneGroups
            .filter { it.act == act }
            .sortedBy { it.name }

    fun getZonesByIds(ids: List<Int>): List<TerrorZone> =
        ids.mapNotNull { id -> terrorZoneGroups.find { it.id == id } }

    fun getAllZoneIds(): Set<Int> = terrorZoneGroups.map { it.id }.toSet()
}