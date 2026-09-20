package com.stefandx.ramscope.ui.processes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.stefandx.ramscope.utils.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProcessesScreen(viewModel: ProcessesViewModel) {
    val processes by viewModel.processes.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.processes_title)) }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text(
                text = stringResource(R.string.processes_disclaimer),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(onClick = { viewModel.refresh() }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Text(text = "  " + stringResource(R.string.refresh))
            }

            LazyColumn(
                modifier = Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(processes) { process ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = process.processName, style = MaterialTheme.typography.titleMedium)
                            MetricRow("PID", process.pid.toString())
                            MetricRow("Importance", process.importance)
                            MetricRow("PSS", process.pss?.let { FormatUtils.formatBytes(it * 1024L) } ?: "Unavailable")
                            MetricRow(
                                "Private dirty",
                                process.privateDirtyKb?.let { FormatUtils.formatBytes(it * 1024L) } ?: "Unavailable"
                            )
                        }
                    }
                }
            }
        }
    }
}
