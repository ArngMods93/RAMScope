package com.stefandx.ramscope.utils

import java.util.Locale
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow

/** Formatting helpers shared by every screen. Kept dependency-free on purpose. */
object FormatUtils {

    private val units = arrayOf("B", "KB", "MB", "GB", "TB")

    /** Renders a byte count as a human-readable string, e.g. "3.7 GB". */
    fun formatBytes(bytes: Long?): String {
        if (bytes == null) return "Unavailable"
        if (bytes <= 0) return "0 B"
        val digitGroups = (log10(abs(bytes).toDouble()) / log10(1024.0)).toInt()
            .coerceIn(0, units.size - 1)
        val value = bytes / 1024.0.pow(digitGroups)
        return String.format(Locale.US, "%.1f %s", value, units[digitGroups])
    }

    fun formatPercent(percent: Int?): String {
        if (percent == null) return "Unavailable"
        return "$percent%"
    }

    fun formatBoolean(value: Boolean?, whenTrue: String, whenFalse: String): String {
        return when (value) {
            true -> whenTrue
            false -> whenFalse
            null -> "Unavailable"
        }
    }
}
