package com.d2rterror.ui.screens.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.CheckCircle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.d2rterror.util.BatteryOptimizationHelper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import kotlinx.coroutines.delay
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.d2rterror.data.local.PreferencesManager
import com.d2rterror.ui.theme.D2RGold
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    bottomPadding: Dp = 0.dp,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Local state for notification times (with debounced save)
    var localNotificationTimes by remember { mutableStateOf(uiState.notificationTimes) }

    // Dialog state for adding new notification time
    var showAddTimeDialog by remember { mutableStateOf(false) }

    // Sync local notification times when uiState loads from storage
    LaunchedEffect(uiState.notificationTimes) {
        localNotificationTimes = uiState.notificationTimes
    }

    // Debounced save for notification times (2 second delay)
    LaunchedEffect(localNotificationTimes) {
        // Skip if it matches the saved state (no change)
        if (localNotificationTimes == uiState.notificationTimes) return@LaunchedEffect

        delay(2000L) // 2 second debounce
        viewModel.saveNotificationTimes(localNotificationTimes)
    }

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            viewModel.setNotificationsEnabled(true)
        }
    }

    // Battery optimization state
    var isIgnoringBatteryOptimizations by remember {
        mutableStateOf(BatteryOptimizationHelper.isIgnoringBatteryOptimizations(context))
    }

    // Re-check battery optimization when screen resumes (user might have changed it in settings)
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        isIgnoringBatteryOptimizations = BatteryOptimizationHelper.isIgnoringBatteryOptimizations(context)
    }

    // Launcher for battery optimization settings
    val batterySettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        // Re-check after returning from settings
        isIgnoringBatteryOptimizations = BatteryOptimizationHelper.isIgnoringBatteryOptimizations(context)
    }

    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val topBarHeight = 64.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Content (draws behind TopAppBar)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    top = statusBarPadding + topBarHeight + 8.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = bottomPadding + 16.dp
                ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Notifications Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Notifications",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Enable/Disable Notifications
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Enable Background Checking",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = if (uiState.notificationsEnabled)
                                    "App checks zones in background and notifies you"
                                else
                                    "No background activity when disabled",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = uiState.notificationsEnabled && hasNotificationPermission,
                            onCheckedChange = { enabled ->
                                if (enabled && !hasNotificationPermission) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                } else {
                                    viewModel.setNotificationsEnabled(enabled)
                                }
                            }
                        )
                    }

                    if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Text(
                            text = "Notification permission required",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    HorizontalDivider()

                    // Zones Selected Info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Zones Selected",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "${uiState.selectedZonesCount} zones",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Timing Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Notification Times",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = { showAddTimeDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add notification time",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Text(
                        text = "Get notified at multiple times before each zone change",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // List of notification times
                    localNotificationTimes.toList().sorted().forEach { minutes ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$minutes minutes before",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            // Only show delete button if more than one time exists
                            if (localNotificationTimes.size > 1) {
                                IconButton(
                                    onClick = {
                                        localNotificationTimes = localNotificationTimes - minutes
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Add Time Dialog
            if (showAddTimeDialog) {
                AddNotificationTimeDialog(
                    existingTimes = localNotificationTimes,
                    onTimeSelected = { minutes ->
                        localNotificationTimes = localNotificationTimes + minutes
                        showAddTimeDialog = false
                    },
                    onDismiss = { showAddTimeDialog = false }
                )
            }

            // Quiet Hours Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bedtime,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Quiet Hours",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Enable/Disable Quiet Hours
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Enable Quiet Hours",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "No notifications during sleep time",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = uiState.quietHoursEnabled,
                            onCheckedChange = { enabled ->
                                viewModel.setQuietHoursEnabled(enabled)
                            }
                        )
                    }

                    // Time pickers (only shown when quiet hours enabled)
                    if (uiState.quietHoursEnabled) {
                        HorizontalDivider()

                        // Start Time
                        var showStartTimePicker by remember { mutableStateOf(false) }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Start Time",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            TextButton(onClick = { showStartTimePicker = true }) {
                                Text(
                                    text = formatMinutesToTime(uiState.quietHoursStart),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (showStartTimePicker) {
                            TimePickerDialog(
                                initialHour = uiState.quietHoursStart / 60,
                                initialMinute = uiState.quietHoursStart % 60,
                                onTimeSelected = { hour, minute ->
                                    viewModel.setQuietHoursStart(hour * 60 + minute)
                                    showStartTimePicker = false
                                },
                                onDismiss = { showStartTimePicker = false }
                            )
                        }

                        // End Time
                        var showEndTimePicker by remember { mutableStateOf(false) }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "End Time",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            TextButton(onClick = { showEndTimePicker = true }) {
                                Text(
                                    text = formatMinutesToTime(uiState.quietHoursEnd),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (showEndTimePicker) {
                            TimePickerDialog(
                                initialHour = uiState.quietHoursEnd / 60,
                                initialMinute = uiState.quietHoursEnd % 60,
                                onTimeSelected = { hour, minute ->
                                    viewModel.setQuietHoursEnd(hour * 60 + minute)
                                    showEndTimePicker = false
                                },
                                onDismiss = { showEndTimePicker = false }
                            )
                        }

                        // Summary
                        Text(
                            text = "Notifications silenced from ${formatMinutesToTime(uiState.quietHoursStart)} to ${formatMinutesToTime(uiState.quietHoursEnd)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Battery Optimization Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isIgnoringBatteryOptimizations)
                        MaterialTheme.colorScheme.surface
                    else
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isIgnoringBatteryOptimizations)
                                Icons.Default.CheckCircle
                            else
                                Icons.Default.BatteryAlert,
                            contentDescription = null,
                            tint = if (isIgnoringBatteryOptimizations)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Background Activity",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isIgnoringBatteryOptimizations)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.error
                        )
                    }

                    if (isIgnoringBatteryOptimizations) {
                        Text(
                            text = "Battery optimization is disabled. Background notifications should work reliably.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    } else {
                        Text(
                            text = "Battery optimization may prevent background notifications. " +
                                    "Disable it for reliable alerts.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Button(
                            onClick = {
                                val intent = BatteryOptimizationHelper
                                    .getRequestIgnoreBatteryOptimizationsIntent(context)
                                batterySettingsLauncher.launch(intent)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Disable Battery Optimization")
                        }

                        // Show manufacturer-specific instructions if available
                        BatteryOptimizationHelper.getManufacturerInstructions()?.let { instructions ->
                            HorizontalDivider()
                            Text(
                                text = "Additional steps for your device:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = instructions,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            TextButton(
                                onClick = {
                                    val intent = BatteryOptimizationHelper.getAppSettingsIntent(context)
                                    batterySettingsLauncher.launch(intent)
                                }
                            ) {
                                Text("Open App Settings")
                            }
                        }
                    }
                }
            }

            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "How it works",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Terror Zones change every 30 minutes (at :00 and :30). " +
                                "The app checks the upcoming zone and notifies you if it matches " +
                                "any of your selected zones.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Data provided by d2emu.com",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // TopAppBar (overlaid on top, semi-transparent)
        TopAppBar(
            title = {
                Text(
                    text = "Settings",
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                titleContentColor = D2RGold
            ),
            modifier = Modifier.statusBarsPadding()
        )
    }
}

/**
 * Format minutes from midnight to a readable time string (e.g., "10:00 PM")
 */
private fun formatMinutesToTime(minutes: Int): String {
    val hour = minutes / 60
    val minute = minutes % 60
    val isPM = hour >= 12
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return "%d:%02d %s".format(displayHour, minute, if (isPM) "PM" else "AM")
}

/**
 * Time Picker Dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onTimeSelected(timePickerState.hour, timePickerState.minute)
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}

/**
 * Dialog for adding a new notification time
 */
@Composable
private fun AddNotificationTimeDialog(
    existingTimes: Set<Int>,
    onTimeSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    // Available times that aren't already selected
    val availableTimes = (PreferencesManager.MIN_ADVANCE_MINUTES..PreferencesManager.MAX_ADVANCE_MINUTES)
        .filter { it !in existingTimes }

    if (availableTimes.isEmpty()) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            },
            title = { Text("All Times Added") },
            text = { Text("You've added all available notification times (1-30 minutes).") }
        )
        return
    }

    var selectedMinutes by remember { mutableIntStateOf(availableTimes.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = { onTimeSelected(selectedMinutes) }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Add Notification Time") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Select how many minutes before zone change to notify:")

                // Slider for selecting minutes
                Column {
                    Slider(
                        value = selectedMinutes.toFloat(),
                        onValueChange = { value ->
                            // Snap to nearest available time
                            val target = value.roundToInt()
                            selectedMinutes = availableTimes.minByOrNull { kotlin.math.abs(it - target) }
                                ?: availableTimes.first()
                        },
                        valueRange = PreferencesManager.MIN_ADVANCE_MINUTES.toFloat()..PreferencesManager.MAX_ADVANCE_MINUTES.toFloat(),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Text(
                        text = "$selectedMinutes minutes before",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    )
}
