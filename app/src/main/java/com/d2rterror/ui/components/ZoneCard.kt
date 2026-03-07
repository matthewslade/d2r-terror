package com.d2rterror.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.d2rterror.data.api.ScrapedZone
import com.d2rterror.data.local.ZoneData
import com.d2rterror.data.model.TerrorZone
import com.d2rterror.ui.theme.D2RGold
import com.d2rterror.ui.theme.D2RRed

@Composable
fun ZoneCard(
    title: String,
    zones: List<ScrapedZone>,
    isCurrentZone: Boolean,
    modifier: Modifier = Modifier,
    countdown: String? = null
) {
    val accentColor = if (isCurrentZone) D2RRed else D2RGold

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )

                if (countdown != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(accentColor.copy(alpha = 0.2f))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = countdown,
                            style = MaterialTheme.typography.labelMedium,
                            color = accentColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Zones
            if (zones.isEmpty()) {
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                // Resolve all matched zone groups from all scraped zones
                val terrorZones = zones.flatMap { zone ->
                    zone.matchedIds.mapNotNull { ZoneData.getZoneById(it) }
                }
                terrorZones.forEachIndexed { index, terrorZone ->
                    ZoneRow(terrorZone = terrorZone)
                    if (index < terrorZones.lastIndex) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ZoneRow(terrorZone: TerrorZone) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Zone name row with act badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = terrorZone.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = terrorZone.actDisplay,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Info row: tier + key + immunities
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TierBadge(tier = terrorZone.tier)
            if (terrorZone.hasBoss) {
                BossIcon()
            }
            if (terrorZone.hasKeyDrop) {
                KeyIcon()
            }
            if (terrorZone.immunities.isNotEmpty()) {
                ImmunityRow(
                    immunities = terrorZone.immunities,
                    dotSize = 16.dp,
                    spacing = 3.dp
                )
            }
        }
    }
}