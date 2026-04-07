package com.d2rterror.data.api

import android.util.Base64
import android.util.Log
import com.d2rterror.data.local.ZoneData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.regex.Pattern

/**
 * Scrapes terror zone data from d2emu.com/tz page.
 */
class TerrorZoneScraper(private val client: OkHttpClient) {

    companion object {
        private const val TAG = "TerrorZoneScraper"
        private const val TRACKER_URL = "https://d2emu.com/tz"
        private const val KEY_1 = "kab2jnb1"
        private const val KEY_2 = "kbd2jnb1"
    }

    /**
     * Fetch terror zone data by scraping d2emu.com
     */
    suspend fun fetchTerrorZones(): TerrorZoneResponse = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(TRACKER_URL)
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36")
            .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            throw Exception("Failed to fetch data: ${response.code}")
        }

        val html = response.body?.string()
            ?: throw Exception("Empty response body")

        parseHtml(html)
    }

    /**
     * Parse the HTML to extract current and next zone data
     */
    private fun parseHtml(html: String): TerrorZoneResponse {
        Log.d(TAG, "HTML length: ${html.length}, first 300 chars: ${html.take(300)}")
        Log.d(TAG, "Contains __1: ${html.contains("__1")}, Contains __2: ${html.contains("__2")}")
        Log.d(TAG, "Contains value=: ${html.contains("value=\"")}")

        // Extract value attributes from __1 and __2 spans
        val currentValue = extractValueAttribute(html, "__1")
        val nextValue = extractValueAttribute(html, "__2")

        Log.d(TAG, "Current value: ${currentValue?.take(50)}...")
        Log.d(TAG, "Next value: ${nextValue?.take(50)}...")

        val currentZones = if (currentValue != null) {
            decryptAndParseZones(currentValue)
        } else emptyList()

        val nextZones = if (nextValue != null) {
            decryptAndParseZones(nextValue)
        } else emptyList()

        return TerrorZoneResponse(
            current = currentZones,
            next = nextZones
        )
    }

    /**
     * Extract the value attribute from a span with given id
     */
    private fun extractValueAttribute(html: String, spanId: String): String? {
        // Pattern: id="__1" value="..."
        val pattern = Pattern.compile("id=\"$spanId\"[^>]*value=\"([^\"]+)\"")
        val matcher = pattern.matcher(html)
        return if (matcher.find()) matcher.group(1) else null
    }

    /**
     * Decrypt the value and parse zone names
     */
    private fun decryptAndParseZones(encryptedValue: String): List<ScrapedZone> {
        return try {
            val decrypted = decrypt(encryptedValue)
            Log.d(TAG, "Decrypted: $decrypted")
            parseZoneNames(decrypted)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to decrypt: ${e.message}")
            emptyList()
        }
    }

    /**
     * Decrypt the XOR-encrypted base64 data
     */
    private fun decrypt(encryptedData: String): String {
        // First base64 decode
        val decoded = Base64.decode(encryptedData, Base64.DEFAULT)

        // XOR with first key
        val intermediate = ByteArray(decoded.size)
        for (i in decoded.indices) {
            intermediate[i] = (decoded[i].toInt() xor KEY_1[i % KEY_1.length].code).toByte()
        }

        // XOR with second key
        val decrypted = ByteArray(intermediate.size)
        for (i in intermediate.indices) {
            decrypted[i] = (intermediate[i].toInt() xor KEY_2[i % KEY_2.length].code).toByte()
        }

        // Result is the final string (no additional base64 decode needed)
        return String(decrypted, Charsets.UTF_8)
    }

    /**
     * Parse the decrypted JSON to extract zone names and match to IDs
     */
    private fun parseZoneNames(json: String): List<ScrapedZone> {
        val result = mutableListOf<ScrapedZone>()

        try {
            val jsonObject = JSONObject(json)
            // The JSON has language codes as keys, we want "enUS"
            val englishZones = jsonObject.optString("enUS", "")

            if (englishZones.isNotEmpty()) {
                // Zone names are separated by </br> or <br>
                val zoneNames = englishZones
                    .replace("</br>", ", ")
                    .replace("<br>", ", ")
                    .trim()

                // Also split to find individual zone names for ID matching
                val individualNames = englishZones
                    .replace("</br>", "|")
                    .replace("<br>", "|")
                    .split("|")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }

                Log.d(TAG, "Found zone names: $individualNames")

                // Find matching terror zone group IDs for all sub-zones
                val matchedIds = mutableListOf<Int>()
                for (name in individualNames) {
                    val zoneId = ZoneData.findZoneByScrapedName(name)
                    if (zoneId != null && zoneId !in matchedIds) {
                        matchedIds.add(zoneId)
                    }
                }

                // Create a single ScrapedZone with full display name and all matched IDs
                result.add(ScrapedZone(
                    displayName = zoneNames,
                    matchedIds = matchedIds
                ))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse JSON: ${e.message}")
        }

        return result
    }
}
