package com.stefandx.ramscope.model

/**
 * Minimal, privacy-respecting view of a running process. Modern Android
 * (API 26+) does not let third-party apps enumerate other apps' memory use,
 * so in practice this only ever describes RAMScope's own process — see the
 * README for details on this platform limitation.
 */
data class ProcessInfo(
    val processName: String,
    val pid: Int,
    val privateDirtyKb: Int?,
    val pss: Int?,
    val importance: String
)
