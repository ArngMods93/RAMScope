package com.stefandx.ramscope.model

/**
 * Snapshot of the device's overall RAM state, as reported by
 * [android.app.ActivityManager.getMemoryInfo].
 *
 * All values are in bytes unless stated otherwise. A `null` value means the
 * metric could not be obtained on this device/API level — callers must show
 * "Unavailable" rather than inventing a number.
 */
data class RamStats(
    val totalBytes: Long?,
    val availableBytes: Long?,
    val thresholdBytes: Long?,
    val isLowMemory: Boolean?
) {
    val usedBytes: Long?
        get() = if (totalBytes != null && availableBytes != null) {
            (totalBytes - availableBytes).coerceAtLeast(0)
        } else null

    val usedPercent: Int?
        get() {
            val total = totalBytes ?: return null
            val used = usedBytes ?: return null
            if (total <= 0) return null
            return ((used.toDouble() / total.toDouble()) * 100).toInt().coerceIn(0, 100)
        }
}
