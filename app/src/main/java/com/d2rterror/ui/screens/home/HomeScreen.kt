package com.d2rterror.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.d2rterror.ui.components.ZoneCard
import com.d2rterror.ui.components.rememberCountdownToNextZone
import com.d2rterror.ui.theme.D2RGold
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    bottomPadding: Dp = 0.dp,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val countdown = rememberCountdownToNextZone()
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val topBarHeight = 64.dp // Standard TopAppBar height

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Content (draws behind TopAppBar)
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = D2RGold
                )
            }

            uiState.error != null -> {
                ErrorContent(
                    message = uiState.error!!,
                    onRetry = { viewModel.retry() },
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
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
                    // Current Zone Card
                    ZoneCard(
                        title = "Current Terror Zone",
                        zones = uiState.current,
                        isCurrentZone = true,
                        countdown = "Active now"
                    )

                    // Next Zone Card
                    ZoneCard(
                        title = "Next Terror Zone",
                        zones = uiState.next,
                        isCurrentZone = false,
                        countdown = countdown
                    )
                }
            }
        }

        // TopAppBar (overlaid on top, semi-transparent)
        TopAppBar(
            title = {
                Text(
                    text = "D2R Terror Zones",
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

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Error",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}
