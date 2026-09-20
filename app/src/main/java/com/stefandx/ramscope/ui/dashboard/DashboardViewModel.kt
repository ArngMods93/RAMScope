package com.stefandx.ramscope.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefandx.ramscope.domain.GetMemorySnapshotUseCase
import com.stefandx.ramscope.model.MemoryDetails
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DashboardUiState(
    val memoryDetails: MemoryDetails? = null,
    val lastUpdatedLabel: String? = null,
    val autoRefreshEnabled: Boolean = false,
    val autoRefreshIntervalSeconds: Int = 5
)

/**
 * Holds dashboard state and drives manual/periodic refreshes. Refreshing is
 * always explicit (the Refresh button) or opt-in (auto refresh toggle) —
 * RAMScope never polls memory in the background when the screen isn't
 * visible.
 */
class DashboardViewModel(
    private val getMemorySnapshot: GetMemorySnapshotUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var autoRefreshJob: kotlinx.coroutines.Job? = null
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    init {
        refresh()
    }

    fun refresh() {
        val snapshot = getMemorySnapshot()
        _uiState.value = _uiState.value.copy(
            memoryDetails = snapshot,
            lastUpdatedLabel = timeFormat.format(Date())
        )
    }

    fun setAutoRefresh(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(autoRefreshEnabled = enabled)
        autoRefreshJob?.cancel()
        if (enabled) {
            autoRefreshJob = viewModelScope.launch {
                while (true) {
                    delay(_uiState.value.autoRefreshIntervalSeconds * 1000L)
                    refresh()
                }
            }
        }
    }

    fun setAutoRefreshInterval(seconds: Int) {
        _uiState.value = _uiState.value.copy(autoRefreshIntervalSeconds = seconds)
        if (_uiState.value.autoRefreshEnabled) {
            setAutoRefresh(true) // restart the loop with the new interval
        }
    }

    override fun onCleared() {
        autoRefreshJob?.cancel()
        super.onCleared()
    }
}
