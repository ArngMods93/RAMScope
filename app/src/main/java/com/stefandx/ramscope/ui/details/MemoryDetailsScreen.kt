package com.stefandx.ramscope.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stefandx.ramscope.R
import com.stefandx.ramscope.ui.components.MetricRow
import com.stefandx.ramscope.ui.components.SectionCard
import com.stefandx.ramscope.ui.dashboard.DashboardViewModel
import com.stefandx.ramscope.utils.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryDetailsScreen(viewModel: DashboardViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val details = state.memoryDetails
    val ram = details?.ramStats
    val zram = details?.zramStats

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.memory_details_title)) }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionCard(title = stringResource(R.string.ram_title)) {
                MetricRow(stringResource(R.string.total), FormatUtils.formatBytes(ram?.totalBytes))
                MetricRow(stringResource(R.string.available), FormatUtils.formatBytes(ram?.availableBytes))
                MetricRow(stringResource(R.string.used), FormatUtils.formatBytes(ram?.usedBytes))
                MetricRow("Used %", FormatUtils.formatPercent(ram?.usedPercent))
                MetricRow(stringResource(R.string.cached), FormatUtils.formatBytes(details?.cachedBytes))
                MetricRow("MemAvailable (kernel)", FormatUtils.formatBytes(details?.memAvailableBytes))
            }

            SectionCard(title = "System state") {
                MetricRow(stringResource(R.string.threshold), FormatUtils.formatBytes(ram?.thresholdBytes))
                MetricRow(
                    stringResource(R.string.low_memory_state),
                    FormatUtils.formatBoolean(
                        ram?.isLowMemory,
                        stringResource(R.string.low_memory_yes),
                        stringResource(R.string.low_memory_no)
                    )
                )
            }

            SectionCard(title = stringResource(R.string.zram_swap_title)) {
                MetricRow(stringResource(R.string.zram_size), FormatUtils.formatBytes(zram?.zramSizeBytes))
                MetricRow(stringResource(R.string.zram_used), FormatUtils.formatBytes(zram?.zramUsedBytes))
                MetricRow(stringResource(R.string.swap_total), FormatUtils.formatBytes(zram?.swapTotalBytes))
                MetricRow(stringResource(R.string.swap_used), FormatUtils.formatBytes(zram?.swapUsedBytes))
                MetricRow(stringResource(R.string.swap_available), FormatUtils.formatBytes(zram?.swapFreeBytes))
            }
        }
    }
}
