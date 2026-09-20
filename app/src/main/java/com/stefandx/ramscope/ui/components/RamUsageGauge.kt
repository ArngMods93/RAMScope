package com.stefandx.ramscope.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Circular indicator showing RAM usage as a percentage. Color shifts from
 * the theme's primary color toward amber/red as usage climbs, which is a
 * purely visual cue — RAMScope never acts on this state automatically.
 */
@Composable
fun RamUsageGauge(
    percent: Int?,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 140.dp
) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val progressColor = when {
        percent == null -> MaterialTheme.colorScheme.outline
        percent >= 90 -> MaterialTheme.colorScheme.error
        percent >= 75 -> androidx.compose.ui.graphics.Color(0xFFE0A526)
        else -> MaterialTheme.colorScheme.primary
    }

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val strokeWidth = 14.dp.toPx()
            val diameter = kotlin.math.min(this.size.width, this.size.height) - strokeWidth
            val topLeft = androidx.compose.ui.geometry.Offset(
                (this.size.width - diameter) / 2f,
                (this.size.height - diameter) / 2f
            )
            val arcSize = Size(diameter, diameter)

            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            val sweep = ((percent ?: 0).coerceIn(0, 100) / 100f) * 360f
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Text(
            text = percent?.let { "$it%" } ?: "—",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
