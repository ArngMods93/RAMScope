package com.stefandx.ramscope.model

/**
 * More technical, combined view of memory information used by the
 * "Memory details" screen. Kept separate from [RamStats] so the dashboard
 * and the details screen can evolve independently.
 */
data class MemoryDetails(
    val ramStats: RamStats,
    val zramStats: ZramStats,
    val cachedBytes: Long?,
    val memAvailableBytes: Long?
)
