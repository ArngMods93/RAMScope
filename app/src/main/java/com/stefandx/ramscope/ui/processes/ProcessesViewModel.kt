package com.stefandx.ramscope.ui.processes

import androidx.lifecycle.ViewModel
import com.stefandx.ramscope.data.MemoryInfoProvider
import com.stefandx.ramscope.model.ProcessInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProcessesViewModel(private val provider: MemoryInfoProvider) : ViewModel() {

    private val _processes = MutableStateFlow<List<ProcessInfo>>(emptyList())
    val processes: StateFlow<List<ProcessInfo>> = _processes.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _processes.value = provider.getVisibleProcesses()
    }
}
