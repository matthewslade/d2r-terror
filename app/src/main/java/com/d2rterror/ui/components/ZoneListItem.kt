package com.d2rterror.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.d2rterror.data.model.TerrorZoneGroup

@Composable
fun ZoneListItem(
    zone: TerrorZoneGroup,
    isSelected: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggle(!isSelected) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = onToggle,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Name row with tier + key
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = zone.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.weight(1f, fill = false)
                )
                TierBadge(tier = zone.tier)
                if (zone.hasBoss) {
                    BossIcon()
                }
                if (zone.hasKeyDrop) {
                    KeyIcon()
                }
            }

            // Sub-zones with individual immunities
            zone.zones.forEach { subZone ->
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = subZone.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    if (subZone.hasBoss) {
                        BossIcon()
                    }
                    if (subZone.hasKeyDrop) {
                        KeyIcon()
                    }
                    if (subZone.immunities.isNotEmpty()) {
                        ImmunityRow(
                            immunities = subZone.immunities,
                            dotSize = 10.dp,
                            spacing = 2.dp
                        )
                    }
                }
            }
        }
    }
}
