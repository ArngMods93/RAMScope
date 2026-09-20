package com.stefandx.ramscope.data

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Debug
import android.os.Process
import com.stefandx.ramscope.model.MemoryDetails
import com.stefandx.ramscope.model.ProcessInfo
import com.stefandx.ramscope.model.RamStats
import com.stefandx.ramscope.model.ZramStats
import java.io.File

/**
 * Single source of truth for every memory metric shown by RAMScope.
 *
 * This class is the only place in the app that talks to Android's memory
 * APIs and to `/proc`. It never uses root, never shells out to restricted
 * commands, and returns `null` for any metric it cannot read safely —
 * the UI layer is responsible for turning that into an "Unavailable" label.
 */
class MemoryInfoProvider(private val context: Context) {

    private val activityManager: ActivityManager
        get() = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    /** Reads total/available/threshold/low-memory state via [ActivityManager]. */
    fun getRamStats(): RamStats {
        val info = ActivityManager.MemoryInfo()
        return try {
            activityManager.getMemoryInfo(info)
            RamStats(
                totalBytes = if (info.totalMem > 0) info.totalMem else null,
                availableBytes = if (info.availMem >= 0) info.availMem else null,
                thresholdBytes = if (info.threshold > 0) info.threshold else null,
                isLowMemory = info.lowMemory
            )
        } catch (e: Exception) {
            RamStats(null, null, null, null)
        }
    }

    /**
     * Parses `/proc/meminfo`, which is world-readable on stock Android and
     * exposes system-wide (not per-app) figures such as cached memory,
     * swap and, on many devices, ZRAM-backed swap totals.
     */
    private fun readProcMeminfo(): Map<String, Long> {
        val result = mutableMapOf<String, Long>()
        try {
            File("/proc/meminfo").forEachLine { line ->
                // Lines look like: "Cached:          123456 kB"
                val parts = line.split(":")
                if (parts.size == 2) {
                    val key = parts[0].trim()
                    val valuePart = parts[1].trim().removeSuffix("kB").trim()
                    valuePart.toLongOrNull()?.let { kb ->
                        result[key] = kb * 1024L
                    }
                }
            }
        } catch (e: Exception) {
            // /proc/meminfo missing or unreadable: leave the map empty,
            // every metric derived from it will surface as "Unavailable".
        }
        return result
    }

    /** Best-effort ZRAM size, read from the sysfs node most devices expose. */
    private fun readZramSizeBytes(): Long? {
        val candidatePaths = listOf(
            "/sys/block/zram0/disksize",
            "/sys/block/zram0/orig_data_size"
        )
        for (path in candidatePaths) {
            try {
                val file = File(path)
                if (file.exists() && file.canRead()) {
                    val value = file.readText().trim().toLongOrNull()
                    if (value != null && value > 0) return value
                }
            } catch (e: Exception) {
                // SELinux commonly blocks this on production builds; move on.
            }
        }
        return null
    }

    /** Best-effort compressed ZRAM usage, in bytes. */
    private fun readZramUsedBytes(): Long? {
        return try {
            val file = File("/sys/block/zram0/mm_stat")
            if (file.exists() && file.canRead()) {
                // mm_stat columns: orig_data_size compr_data_size mem_used_total ...
                val columns = file.readText().trim().split(Regex("\\s+"))
                columns.getOrNull(2)?.toLongOrNull()
            } else null
        } catch (e: Exception) {
            null
        }
    }

    fun getZramStats(): ZramStats {
        val meminfo = readProcMeminfo()
        val swapTotal = meminfo["SwapTotal"]
        val swapFree = meminfo["SwapFree"]
        val swapUsed = if (swapTotal != null && swapFree != null) {
            (swapTotal - swapFree).coerceAtLeast(0)
        } else null

        return ZramStats(
            zramSizeBytes = readZramSizeBytes(),
            zramUsedBytes = readZramUsedBytes(),
            swapTotalBytes = swapTotal,
            swapUsedBytes = swapUsed,
            swapFreeBytes = swapFree
        )
    }

    fun getMemoryDetails(): MemoryDetails {
        val meminfo = readProcMeminfo()
        return MemoryDetails(
            ramStats = getRamStats(),
            zramStats = getZramStats(),
            cachedBytes = meminfo["Cached"],
            memAvailableBytes = meminfo["MemAvailable"]
        )
    }

    /**
     * Returns whatever process-level information the platform legally
     * exposes to a third-party app. Since Android 8 (API 26), apps can no
     * longer query other apps' running processes or memory usage — this
     * will effectively always be a single entry describing RAMScope
     * itself. See README.md → "Limitaciones de las APIs de Android".
     */
    fun getVisibleProcesses(): List<ProcessInfo> {
        val pid = Process.myPid()
        val memoryInfo = try {
            val info = Debug.MemoryInfo()
            Debug.getMemoryInfo(info)
            info
        } catch (e: Exception) {
            null
        }

        val runningProcesses = try {
            activityManager.runningAppProcesses
        } catch (e: Exception) {
            null
        }

        val self = runningProcesses?.firstOrNull { it.pid == pid }

        val importance = when (self?.importance) {
            ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND -> "Foreground"
            ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE -> "Visible"
            ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE -> "Service"
            ActivityManager.RunningAppProcessInfo.IMPORTANCE_CACHED -> "Cached"
            null -> "Unknown"
            else -> "Background"
        }

        return listOf(
            ProcessInfo(
                processName = self?.processName ?: context.packageName,
                pid = pid,
                privateDirtyKb = memoryInfo?.totalPrivateDirty,
                pss = memoryInfo?.totalPss,
                importance = importance
            )
        )
    }

    companion object {
        val sdkInt: Int = Build.VERSION.SDK_INT
    }
}
