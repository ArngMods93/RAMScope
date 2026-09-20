package com.stefandx.ramscope.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stefandx.ramscope.R
import com.stefandx.ramscope.ui.components.MetricRow
import com.stefandx.ramscope.ui.components.RamUsageGauge
import com.stefandx.ramscope.ui.components.SectionCard
import com.stefandx.ramscope.utils.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val ramStats = state.memoryDetails?.ramStats

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.app_name)) })
        }
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    RamUsageGauge(percent = ramStats?.usedPercent)
                }
                MetricRow(stringResource(R.string.total), FormatUtils.formatBytes(ramStats?.totalBytes))
                MetricRow(stringResource(R.string.used), FormatUtils.formatBytes(ramStats?.usedBytes))
                MetricRow(stringResource(R.string.available), FormatUtils.formatBytes(ramStats?.availableBytes))
                MetricRow(stringResource(R.string.cached), FormatUtils.formatBytes(state.memoryDetails?.cachedBytes))
            }

            SectionCard(title = "System state") {
                MetricRow(
                    stringResource(R.string.low_memory_state),
                    FormatUtils.formatBoolean(
                        ramStats?.isLowMemory,
                        stringResource(R.string.low_memory_yes),
                        stringResource(R.string.low_memory_no)
                    )
                )
                MetricRow(stringResource(R.string.threshold), FormatUtils.formatBytes(ramStats?.thresholdBytes))
            }

            SectionCard(title = "Refresh") {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Button(onClick = { viewModel.refresh() }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Text(text = "  " + stringResource(R.string.refresh))
                    }

                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.auto_refresh), style = MaterialTheme.typography.bodyLarge)
                        Switch(
                            checked = state.autoRefreshEnabled,
                            onCheckedChange = { viewModel.setAutoRefresh(it) }
                        )
                    }

                    state.lastUpdatedLabel?.let {
                        Text(
                            text = stringResource(R.string.last_updated, it),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
