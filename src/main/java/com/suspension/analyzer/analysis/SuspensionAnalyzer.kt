package com.suspension.analyzer.analysis

import com.suspension.analyzer.sensors.SensorDataManager
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Analyzes suspension test data and computes professional metrics and composite score.
 */
object SuspensionAnalyzer {
    data class Metrics(
        val rmsVertical: Double,
        val peakVertical: Double,
        val vibrationFrequency: Double,
        val dampingRatio: Double,
        val speedBucket: Int, // 0: 0-30, 1: 30-60, 2: 60+
        val normalizedRMS: Double,
        val normalizedPeak: Double,
        val compositeScore: Double
    )

    /**
     * Analyze a list of SensorReadings and return metrics per speed bucket.
     */
    fun analyze(readings: List<SensorDataManager.SensorReading>): List<Metrics> {
        // Group by speed bucket
        val buckets = listOf(
            readings.filter { it.speed * 3.6 < 30 },
            readings.filter { it.speed * 3.6 in 30.0..60.0 },
            readings.filter { it.speed * 3.6 > 60 }
        )
        return buckets.mapIndexed { idx, bucket ->
            if (bucket.isEmpty()) return@mapIndexed Metrics(0.0, 0.0, 0.0, 0.0, idx, 0.0, 0.0, 100.0)
            val z = bucket.map { it.filteredAccelZ.toDouble() }
            val speed = bucket.map { it.speed.toDouble() }.average().coerceAtLeast(1.0)
            val rms = sqrt(z.map { it * it }.average())
            val peak = z.maxOf { abs(it) }
            val freq = estimateFrequency(z, bucket.map { it.timestamp })
            val damping = estimateDamping(z, bucket.map { it.timestamp })
            val normRMS = rms / (speed.pow(2) / 1000.0)
            val normPeak = peak / (speed.pow(2) / 1000.0)
            val score = (100.0 - (0.5 * normRMS + 0.3 * normPeak + 0.2 * freq)).coerceIn(0.0, 100.0)
            Metrics(rms, peak, freq, damping, idx, normRMS, normPeak, score)
        }
    }

    private fun estimateFrequency(z: List<Double>, timestamps: List<Long>): Double {
        if (z.size < 2) return 0.0
        // Simple zero-crossing peak counting
        var count = 0
        for (i in 1 until z.size - 1) {
            if ((z[i - 1] < z[i] && z[i] > z[i + 1]) || (z[i - 1] > z[i] && z[i] < z[i + 1])) {
                count++
            }
        }
        val duration = (timestamps.last() - timestamps.first()).toDouble() / 1000.0
        return if (duration > 0) count / duration else 0.0
    }

    private fun estimateDamping(z: List<Double>, timestamps: List<Long>): Double {
        // Logarithmic decrement on peak decay (approximate)
        val peaks = mutableListOf<Double>()
        for (i in 1 until z.size - 1) {
            if (z[i] > z[i - 1] && z[i] > z[i + 1]) peaks.add(z[i])
        }
        if (peaks.size < 2) return 0.0
        val deltas = peaks.zipWithNext { a, b -> ln(a / b).takeIf { b != 0.0 && a > b } ?: 0.0 }
        val avgDelta = deltas.filter { it > 0 }.averageOrNull() ?: 0.0
        return avgDelta / (2 * Math.PI)
    }

    private fun List<Double>.averageOrNull(): Double? = if (isNotEmpty()) average() else null
}
