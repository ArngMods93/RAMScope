package com.stefandx.ramscope.model

/**
 * Snapshot of ZRAM and swap usage, parsed from `/proc/meminfo` and, when
 * accessible, `/sys/block/zram0`. Any field can be `null` if the device does
 * not expose it, the file is not readable without elevated privileges, or
 * the device has no ZRAM/swap configured at all.
 */
data class ZramStats(
    val zramSizeBytes: Long?,
    val zramUsedBytes: Long?,
    val swapTotalBytes: Long?,
    val swapUsedBytes: Long?,
    val swapFreeBytes: Long?
)
