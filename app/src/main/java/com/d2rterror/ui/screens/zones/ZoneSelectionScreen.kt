package com.d2rterror.ui.screens.zones

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.IndeterminateCheckBox
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.d2rterror.data.model.Element
import com.d2rterror.data.model.TerrorZone
import com.d2rterror.ui.components.ZoneListItem
import com.d2rterror.ui.components.color
import com.d2rterror.ui.components.displayName
import com.d2rterror.ui.theme.D2RGold
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ZoneSelectionScreen(
    bottomPadding: Dp = 0.dp,
    viewModel: ZoneSelectionViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "My Zones",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${uiState.selectedZoneIds.size} zones selected",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                    titleContentColor = D2RGold
                ),
                actions = {
                    TextButton(onClick = { viewModel.selectAll() }) {
                        Text("All")
                    }
                    TextButton(onClick = { viewModel.clearAll() }) {
                        Text("None")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(),
                bottom = bottomPadding + 8.dp
            )
        ) {
            // Immunity filter section
            item(key = "immunity_filter") {
                ImmunityFilterRow(
                    excludedImmunities = uiState.excludedImmunities,
                    onToggle = { viewModel.toggleImmunityFilter(it) },
                    onClear = { viewModel.clearFilters() }
                )
            }

            // Use filtered zones for display
            uiState.filteredZonesByAct.forEach { (act, zones) ->
                item(key = "act_header_$act") {
                    ActHeader(
                        act = act,
                        zones = zones,
                        selectedCount = zones.count { it.id in uiState.selectedZoneIds },
                        isExpanded = act in uiState.expandedActs,
                        onToggleExpand = { viewModel.toggleActExpanded(act) },
                        onToggleSelection = {
                            val allSelected = zones.all { it.id in uiState.selectedZoneIds }
                            if (allSelected) {
                                viewModel.clearAllInAct(act)
                            } else {
                                viewModel.selectAllInAct(act)
                            }
                        }
                    )
                }

                if (act in uiState.expandedActs) {
                    items(
                        items = zones,
                        key = { zone -> zone.id }
                    ) { zone ->
                        ZoneListItem(
                            zone = zone,
                            isSelected = zone.id in uiState.selectedZoneIds,
                            onToggle = { isSelected ->
                                viewModel.toggleZone(zone.id, isSelected)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ImmunityFilterRow(
    excludedImmunities: Set<Element>,
    onToggle: (Element) -> Unit,
    onClear: () -> Unit
) {
    val elements = listOf(
        Element.PHYSICAL, Element.MAGIC,
        Element.FIRE, Element.COLD, Element.LIGHTNING, Element.POISON
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Exclude Immunities",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            if (excludedImmunities.isNotEmpty()) {
                Text(
                    text = "Clear",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { onClear() }
                        .padding(4.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            elements.forEach { element ->
                val isExcluded = element in excludedImmunities
                val color = element.color()
                ImmunityFilterChip(
                    label = element.displayName(),
                    color = color,
                    isExcluded = isExcluded,
                    onClick = { onToggle(element) }
                )
            }
        }
    }
}

@Composable
private fun ImmunityFilterChip(
    label: String,
    color: Color,
    isExcluded: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isExcluded) color.copy(alpha = 0.3f) else Color.Transparent
    val borderColor = if (isExcluded) color else color.copy(alpha = 0.4f)
    val textColor = if (isExcluded) color else color.copy(alpha = 0.6f)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = if (isExcluded) "No $label" else label,
                fontSize = 10.sp,
                fontWeight = if (isExcluded) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )
        }
    }
}

@Composable
private fun ActHeader(
    act: Int,
    zones: List<TerrorZone>,
    selectedCount: Int,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onToggleSelection: () -> Unit
) {
    val actName = when (act) {
        1 -> "Act I"
        2 -> "Act II"
        3 -> "Act III"
        4 -> "Act IV"
        5 -> "Act V"
        else -> "Act $act"
    }

    val allSelected = selectedCount == zones.size
    val noneSelected = selectedCount == 0
    val someSelected = !allSelected && !noneSelected

    val checkboxIcon = when {
        allSelected -> Icons.Default.CheckBox
        someSelected -> Icons.Default.IndeterminateCheckBox
        else -> Icons.Default.CheckBoxOutlineBlank
    }

    val checkboxTint = when {
        allSelected -> MaterialTheme.colorScheme.primary
        someSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val checkboxDescription = when {
        allSelected -> "Deselect all"
        else -> "Select all"
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleExpand() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = actName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "($selectedCount/${zones.size})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = onToggleSelection,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = checkboxIcon,
                    contentDescription = checkboxDescription,
                    tint = checkboxTint,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}