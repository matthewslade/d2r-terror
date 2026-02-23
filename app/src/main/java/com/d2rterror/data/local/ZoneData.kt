package com.d2rterror.data.local

import com.d2rterror.data.model.TerrorZone

/**
 * Terror Zone Groups for Diablo 2 Resurrected.
 *
 * Source: https://www.icy-veins.com/d2/terror-zones-guide
 *
 * Terror zones terrorize groups of connected areas together.
 * Each group has a display name (what users see) and keywords for matching
 * against scraped zone names from d2emu.com.
 */
object ZoneData {

    /**
     * Official terror zone groups (36 total).
     * Each group represents one selectable notification option.
     * Keywords are used to match against scraped zone names.
     */
    private val terrorZoneGroups: List<TerrorZone> = listOf(
        // Act I - 12 terror zones
        TerrorZone(1, "Blood Moor and Den of Evil", 1,
            listOf("blood moor", "den of evil")),
        TerrorZone(2, "Cold Plains and The Cave", 1,
            listOf("cold plains", "cave")),
        TerrorZone(3, "Burial Grounds, The Crypt, and the Mausoleum", 1,
            listOf("burial grounds", "crypt", "mausoleum")),
        TerrorZone(4, "Stony Field", 1,
            listOf("stony field")),
        TerrorZone(5, "Dark Wood and Underground Passage", 1,
            listOf("dark wood", "underground passage")),
        TerrorZone(6, "Black Marsh and The Hole", 1,
            listOf("black marsh", "hole")),
        TerrorZone(7, "The Forgotten Tower", 1,
            listOf("forgotten tower", "tower cellar")),
        TerrorZone(8, "Jail and Barracks", 1,
            listOf("jail", "barracks")),
        TerrorZone(9, "Cathedral and Catacombs", 1,
            listOf("cathedral", "catacombs", "inner cloister")),
        TerrorZone(10, "The Pit", 1,
            listOf("pit", "tamoe highland")),
        TerrorZone(11, "Tristram", 1,
            listOf("tristram")),
        TerrorZone(12, "Moo Moo Farm", 1,
            listOf("moo moo farm", "secret cow level", "cow level")),

        // Act II - 8 terror zones
        TerrorZone(13, "Sewers", 2,
            listOf("sewers", "sewer")),
        TerrorZone(14, "Rocky Waste and Stony Tomb", 2,
            listOf("rocky waste", "stony tomb")),
        TerrorZone(15, "Dry Hills and Halls of the Dead", 2,
            listOf("dry hills", "halls of the dead")),
        TerrorZone(16, "Far Oasis", 2,
            listOf("far oasis")),
        TerrorZone(17, "Lost City, Valley of Snakes, and Claw Viper Temple", 2,
            listOf("lost city", "valley of snakes", "claw viper")),
        TerrorZone(18, "Ancient Tunnels", 2,
            listOf("ancient tunnels")),
        TerrorZone(19, "Arcane Sanctuary", 2,
            listOf("arcane sanctuary")),
        TerrorZone(20, "Tal Rasha's Tombs", 2,
            listOf("tal rasha", "tomb")),

        // Act III - 6 terror zones
        TerrorZone(21, "Spider Forest and Spider Cavern", 3,
            listOf("spider forest", "spider cavern", "arachnid lair")),
        TerrorZone(22, "Great Marsh", 3,
            listOf("great marsh")),
        TerrorZone(23, "Flayer Jungle and Flayer Dungeon", 3,
            listOf("flayer jungle", "flayer dungeon", "swampy pit")),
        TerrorZone(24, "Kurast Bazaar, Ruined Temple, and Disused Fane", 3,
            listOf("kurast bazaar", "ruined temple", "disused fane")),
        TerrorZone(25, "Travincal", 3,
            listOf("travincal")),
        TerrorZone(26, "Durance of Hate", 3,
            listOf("durance of hate")),

        // Act IV - 3 terror zones
        TerrorZone(27, "Outer Steppes and Plains of Despair", 4,
            listOf("outer steppes", "plains of despair")),
        TerrorZone(28, "River of Flame and City of the Damned", 4,
            listOf("river of flame", "city of the damned")),
        TerrorZone(29, "Chaos Sanctuary", 4,
            listOf("chaos sanctuary")),

        // Act V - 7 terror zones
        TerrorZone(30, "Bloody Foothills, Frigid Highlands, and Abaddon", 5,
            listOf("bloody foothills", "frigid highlands", "abaddon")),
        TerrorZone(31, "Glacial Trail and Drifter Cavern", 5,
            listOf("glacial trail", "drifter cavern")),
        TerrorZone(32, "Crystalline Passage and Frozen River", 5,
            listOf("crystalline passage", "frozen river")),
        TerrorZone(33, "Arreat Plateau and Pit of Acheron", 5,
            listOf("arreat plateau", "pit of acheron")),
        TerrorZone(34, "Nihlathak's Temple and Halls", 5,
            listOf("nihlathak", "halls of anguish", "halls of pain", "halls of vaught")),
        TerrorZone(35, "Ancient's Way and Icy Cellar", 5,
            listOf("ancient's way", "ancients way", "icy cellar")),
        TerrorZone(36, "Worldstone Keep, Throne of Destruction, and Worldstone Chamber", 5,
            listOf("worldstone keep", "throne of destruction", "worldstone chamber"))
    )

    /**
     * Find which terror zone group matches the given scraped zone name.
     * Returns the zone ID if found, null otherwise.
     */
    fun findZoneByScrapedName(scrapedName: String): Int? {
        val normalizedName = scrapedName.lowercase().trim()
        android.util.Log.d("ZoneData", "Finding zone for scraped name: '$scrapedName' -> normalized: '$normalizedName'")

        // Check each zone's keywords
        for (zone in terrorZoneGroups) {
            for (keyword in zone.keywords) {
                // Check if the keyword matches the scraped name
                if (normalizedName == keyword || normalizedName.contains(keyword) || keyword.contains(normalizedName)) {
                    android.util.Log.d("ZoneData", "MATCHED: '$normalizedName' -> Zone ${zone.id} '${zone.name}' via keyword '$keyword'")
                    return zone.id
                }
            }
        }

        android.util.Log.w("ZoneData", "NO MATCH for: '$normalizedName'")
        return null
    }

    /**
     * Get a TerrorZone by its ID
     */
    fun getZoneById(id: Int): TerrorZone? = terrorZoneGroups.find { it.id == id }

    /**
     * Get all zones as a list, sorted by Act then name
     */
    fun getAllZones(): List<TerrorZone> = terrorZoneGroups
        .sortedWith(compareBy({ it.act }, { it.name }))

    /**
     * Get zones grouped by Act
     */
    fun getZonesByAct(): Map<Int, List<TerrorZone>> = terrorZoneGroups
        .groupBy { it.act }
        .mapValues { (_, zones) -> zones.sortedBy { it.name } }

    /**
     * Get zones for a specific Act
     */
    fun getZonesForAct(act: Int): List<TerrorZone> =
        terrorZoneGroups
            .filter { it.act == act }
            .sortedBy { it.name }

    /**
     * Convert a list of zone IDs to TerrorZone objects
     */
    fun getZonesByIds(ids: List<Int>): List<TerrorZone> =
        ids.mapNotNull { id -> terrorZoneGroups.find { it.id == id } }

    /**
     * Get all zone IDs
     */
    fun getAllZoneIds(): Set<Int> = terrorZoneGroups.map { it.id }.toSet()
}
