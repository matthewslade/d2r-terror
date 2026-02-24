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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.d2rterror.data.local.PreferencesManager
import com.d2rterror.ui.theme.D2RGold
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Local state for editing - initialized from uiState
    var localNotificationsEnabled by remember { mutableStateOf(uiState.notificationsEnabled) }
    var localAdvanceMinutes by remember { mutableIntStateOf(uiState.advanceNotificationMinutes) }
    var localQuietHoursEnabled by remember { mutableStateOf(uiState.quietHoursEnabled) }
    var localQuietHoursStart by remember { mutableIntStateOf(uiState.quietHoursStart) }
    var localQuietHoursEnd by remember { mutableIntStateOf(uiState.quietHoursEnd) }

    // Sync local state when uiState loads from storage (only on initial load)
    LaunchedEffect(Unit) {
        viewModel.uiState.collect { state ->
            localNotificationsEnabled = state.notificationsEnabled
            localAdvanceMinutes = state.advanceNotificationMinutes
            localQuietHoursEnabled = state.quietHoursEnabled
            localQuietHoursStart = state.quietHoursStart
            localQuietHoursEnd = state.quietHoursEnd
        }
    }

    // Check if there are unsaved changes
    val hasChanges = localNotificationsEnabled != uiState.notificationsEnabled ||
            localAdvanceMinutes != uiState.advanceNotificationMinutes ||
            localQuietHoursEnabled != uiState.quietHoursEnabled ||
            localQuietHoursStart != uiState.quietHoursStart ||
            localQuietHoursEnd != uiState.quietHoursEnd

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Save button - only enabled when there are changes
                    IconButton(
                        onClick = {
                            viewModel.saveAllSettings(
                                notificationsEnabled = localNotificationsEnabled,
                                advanceMinutes = localAdvanceMinutes,
                                quietHoursEnabled = localQuietHoursEnabled,
                                quietHoursStart = localQuietHoursStart,
                                quietHoursEnd = localQuietHoursEnd
                            )
                            onNavigateBack()
                        },
                        enabled = hasChanges
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save",
                            tint = if (hasChanges) D2RGold else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = D2RGold
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
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
                                text = if (localNotificationsEnabled)
                                    "App checks zones in background and notifies you"
                                else
                                    "No background activity when disabled",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = localNotificationsEnabled && hasNotificationPermission,
                            onCheckedChange = { enabled ->
                                if (enabled && !hasNotificationPermission) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                } else {
                                    localNotificationsEnabled = enabled
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Notification Timing",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = "Get notified $localAdvanceMinutes minutes before zone becomes active",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Slider for advance notification time
                    Column {
                        Slider(
                            value = localAdvanceMinutes.toFloat(),
                            onValueChange = { value ->
                                localAdvanceMinutes = value.roundToInt()
                            },
                            valueRange = PreferencesManager.MIN_ADVANCE_MINUTES.toFloat()..PreferencesManager.MAX_ADVANCE_MINUTES.toFloat(),
                            steps = PreferencesManager.MAX_ADVANCE_MINUTES - PreferencesManager.MIN_ADVANCE_MINUTES - 1,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${PreferencesManager.MIN_ADVANCE_MINUTES} min",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$localAdvanceMinutes min",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${PreferencesManager.MAX_ADVANCE_MINUTES} min",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
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
                            checked = localQuietHoursEnabled,
                            onCheckedChange = { enabled ->
                                localQuietHoursEnabled = enabled
                            }
                        )
                    }

                    // Time pickers (only shown when quiet hours enabled)
                    if (localQuietHoursEnabled) {
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
                                    text = formatMinutesToTime(localQuietHoursStart),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (showStartTimePicker) {
                            TimePickerDialog(
                                initialHour = localQuietHoursStart / 60,
                                initialMinute = localQuietHoursStart % 60,
                                onTimeSelected = { hour, minute ->
                                    localQuietHoursStart = hour * 60 + minute
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
                                    text = formatMinutesToTime(localQuietHoursEnd),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (showEndTimePicker) {
                            TimePickerDialog(
                                initialHour = localQuietHoursEnd / 60,
                                initialMinute = localQuietHoursEnd % 60,
                                onTimeSelected = { hour, minute ->
                                    localQuietHoursEnd = hour * 60 + minute
                                    showEndTimePicker = false
                                },
                                onDismiss = { showEndTimePicker = false }
                            )
                        }

                        // Summary
                        Text(
                            text = "Notifications silenced from ${formatMinutesToTime(localQuietHoursStart)} to ${formatMinutesToTime(localQuietHoursEnd)}",
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
