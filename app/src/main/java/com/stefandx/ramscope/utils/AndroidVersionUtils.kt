package com.stefandx.ramscope.utils

import android.os.Build

/**
 * Small helper to keep API-level checks consistent and readable across the
 * codebase, per the project's compatibility strategy: detect the version,
 * use a compatible alternative when one exists, otherwise surface
 * "Unavailable" instead of guessing.
 */
object AndroidVersionUtils {
    fun isAtLeast(apiLevel: Int): Boolean = Build.VERSION.SDK_INT >= apiLevel

    val readableVersion: String
        get() = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
}
