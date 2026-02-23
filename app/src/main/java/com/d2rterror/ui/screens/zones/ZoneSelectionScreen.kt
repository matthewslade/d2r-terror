package com.d2rterror.ui.screens.zones

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.d2rterror.data.model.TerrorZone
import com.d2rterror.ui.components.ZoneListItem
import com.d2rterror.ui.theme.D2RGold
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoneSelectionScreen(
    onNavigateBack: () -> Unit,
    viewModel: ZoneSelectionViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Zone Selection",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${uiState.selectedZoneIds.size} zones selected",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
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
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            uiState.zonesByAct.forEach { (act, zones) ->
                item(key = "act_header_$act") {
                    ActHeader(
                        act = act,
                        zones = zones,
                        selectedCount = zones.count { it.id in uiState.selectedZoneIds },
                        isExpanded = act in uiState.expandedActs,
                        onToggleExpand = { viewModel.toggleActExpanded(act) },
                        onSelectAll = { viewModel.selectAllInAct(act) },
                        onClearAll = { viewModel.clearAllInAct(act) }
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

@Composable
private fun ActHeader(
    act: Int,
    zones: List<TerrorZone>,
    selectedCount: Int,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onSelectAll: () -> Unit,
    onClearAll: () -> Unit
) {
    val actName = when (act) {
        1 -> "Act I"
        2 -> "Act II"
        3 -> "Act III"
        4 -> "Act IV"
        5 -> "Act V"
        else -> "Act $act"
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

            Row {
                IconButton(
                    onClick = onSelectAll,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckBox,
                        contentDescription = "Select All",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onClearAll,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckBoxOutlineBlank,
                        contentDescription = "Clear All",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
