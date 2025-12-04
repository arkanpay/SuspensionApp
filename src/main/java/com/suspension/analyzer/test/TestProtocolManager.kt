package com.suspension.analyzer.test

import com.suspension.analyzer.sensors.SensorDataManager
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

/**
 * Manages standardized test protocols and scoring
 */
class TestProtocolManager {

    sealed class TestProtocol(
        val name: String,
        val description: String,
        val durationSeconds: Int,
        val instructions: List<String>
    ) {
        abstract fun calculateScore(metrics: TestMetrics): TestScore
    }

    /**
     * Standing Start/Stop Test
     * Tests acceleration and braking performance
     */
    class StandingStartStopTest : TestProtocol(
        name = "Standing Start/Stop",
        description = "Accelerate from stop to 40mph, then brake back to stop",
        durationSeconds = 30,
        instructions = listOf(
            "1. Start from complete stop",
            "2. Accelerate smoothly to 40 mph",
            "3. Brake smoothly to complete stop",
            "4. Test evaluates acceleration and braking G-forces"
        )
    ) {
        override fun calculateScore(metrics: TestMetrics): TestScore {
            // Scoring based on:
            // - Peak longitudinal G during acceleration (higher = better performance)
            // - Peak longitudinal G during braking (higher = better stopping)
            // - Smoothness (lower variation = better suspension control)

            val accelScore = when {
                metrics.peakAccelerationG > 0.8f -> 100
                metrics.peakAccelerationG > 0.6f -> 85
                metrics.peakAccelerationG > 0.4f -> 70
                metrics.peakAccelerationG > 0.2f -> 50
                else -> 30
            }

            val brakeScore = when {
                abs(metrics.peakBrakingG) > 0.9f -> 100
                abs(metrics.peakBrakingG) > 0.7f -> 85
                abs(metrics.peakBrakingG) > 0.5f -> 70
                abs(metrics.peakBrakingG) > 0.3f -> 50
                else -> 30
            }

            val smoothnessScore = when {
                metrics.verticalVariation < 0.15f -> 100
                metrics.verticalVariation < 0.25f -> 85
                metrics.verticalVariation < 0.35f -> 70
                metrics.verticalVariation < 0.50f -> 50
                else -> 30
            }

            val overallScore = (accelScore * 0.4 + brakeScore * 0.4 + smoothnessScore * 0.2).toInt()

            return TestScore(
                overallScore = overallScore,
                accelerationScore = accelScore,
                brakingScore = brakeScore,
                corneringScore = null,
                smoothnessScore = smoothnessScore,
                rating = getRating(overallScore),
                breakdown = mapOf(
                    "Peak Acceleration" to "${String.format("%.2f", metrics.peakAccelerationG)} G",
                    "Peak Braking" to "${String.format("%.2f", abs(metrics.peakBrakingG))} G",
                    "Vertical Stability" to "${String.format("%.2f", metrics.verticalVariation)} G variation"
                )
            )
        }
    }

    /**
     * Figure-8 Test
     * Tests cornering performance and body control
     */
    class Figure8Test : TestProtocol(
        name = "Figure-8",
        description = "Drive a figure-8 pattern at consistent speed",
        durationSeconds = 45,
        instructions = listOf(
            "1. Find safe, empty area (parking lot)",
            "2. Drive figure-8 pattern",
            "3. Maintain consistent 20-25 mph",
            "4. Complete at least 2 full figure-8s",
            "5. Test evaluates cornering and body roll control"
        )
    ) {
        override fun calculateScore(metrics: TestMetrics): TestScore {
            // Scoring based on:
            // - Peak lateral G during turns (higher = better cornering grip)
            // - Lateral consistency (lower variation = better control)
            // - Body roll control (vertical stability during turns)
            // - Overall smoothness

            val corneringScore = when {
                metrics.peakLateralG > 0.7f -> 100
                metrics.peakLateralG > 0.5f -> 85
                metrics.peakLateralG > 0.4f -> 70
                metrics.peakLateralG > 0.3f -> 50
                else -> 30
            }

            val consistencyScore = when {
                metrics.lateralVariation < 0.15f -> 100
                metrics.lateralVariation < 0.25f -> 85
                metrics.lateralVariation < 0.35f -> 70
                metrics.lateralVariation < 0.50f -> 50
                else -> 30
            }

            val bodyRollScore = when {
                metrics.verticalVariation < 0.20f -> 100
                metrics.verticalVariation < 0.30f -> 85
                metrics.verticalVariation < 0.40f -> 70
                metrics.verticalVariation < 0.55f -> 50
                else -> 30
            }

            val overallScore = (corneringScore * 0.5 + consistencyScore * 0.3 + bodyRollScore * 0.2).toInt()

            return TestScore(
                overallScore = overallScore,
                accelerationScore = null,
                brakingScore = null,
                corneringScore = corneringScore,
                smoothnessScore = bodyRollScore,
                rating = getRating(overallScore),
                breakdown = mapOf(
                    "Peak Lateral G" to "${String.format("%.2f", metrics.peakLateralG)} G",
                    "Cornering Consistency" to "${String.format("%.2f", metrics.lateralVariation)} G variation",
                    "Body Roll Control" to "${String.format("%.2f", metrics.verticalVariation)} G variation",
                    "Turn Count" to "${metrics.turnCount} turns detected"
                )
            )
        }
    }

    data class TestMetrics(
        val peakLateralG: Float,
        val peakAccelerationG: Float,
        val peakBrakingG: Float,
        val peakVerticalG: Float,
        val lateralVariation: Float,
        val longitudinalVariation: Float,
        val verticalVariation: Float,
        val turnCount: Int,
        val durationSeconds: Int,
        val dataPoints: Int
    )

    data class TestScore(
        val overallScore: Int,  // 0-100
        val accelerationScore: Int?,
        val brakingScore: Int?,
        val corneringScore: Int?,
        val smoothnessScore: Int,
        val rating: String,  // "Excellent", "Good", "Average", "Poor"
        val breakdown: Map<String, String>
    )

    companion object {
        fun getRating(score: Int): String = when {
            score >= 90 -> "Excellent"
            score >= 75 -> "Good"
            score >= 60 -> "Average"
            score >= 40 -> "Fair"
            else -> "Poor"
        }

        fun getAvailableProtocols(): List<TestProtocol> = listOf(
            StandingStartStopTest(),
            Figure8Test()
        )
    }

    /**
     * Analyzes collected sensor data and computes test metrics
     */
    class TestAnalyzer {
        private val lateralGValues = mutableListOf<Float>()
        private val longitudinalGValues = mutableListOf<Float>()
        private val verticalGValues = mutableListOf<Float>()
        private var startTime: Long = 0L
        private var peakLateral = 0f
        private var peakAcceleration = 0f
        private var peakBraking = 0f
        private var peakVertical = 0f
        private var turnCount = 0
        private var lastLateralDirection = 0  // -1 = left, 1 = right, 0 = straight

        fun startAnalysis() {
            lateralGValues.clear()
            longitudinalGValues.clear()
            verticalGValues.clear()
            startTime = System.currentTimeMillis()
            peakLateral = 0f
            peakAcceleration = 0f
            peakBraking = 0f
            peakVertical = 0f
            turnCount = 0
            lastLateralDirection = 0
        }

        fun processSensorReading(reading: SensorDataManager.SensorReading) {
            lateralGValues.add(reading.peakLateralG)
            longitudinalGValues.add(reading.peakLongitudinalG)
            verticalGValues.add(reading.peakVerticalG)

            // Track peaks
            peakLateral = max(peakLateral, abs(reading.peakLateralG))
            peakVertical = max(peakVertical, abs(reading.peakVerticalG))

            // Separate acceleration from braking
            if (reading.peakLongitudinalG > 0) {
                peakAcceleration = max(peakAcceleration, reading.peakLongitudinalG)
            } else {
                peakBraking = kotlin.math.min(peakBraking, reading.peakLongitudinalG)
            }

            // Count direction changes (turns)
            val currentDirection = when {
                reading.peakLateralG > 0.2f -> 1
                reading.peakLateralG < -0.2f -> -1
                else -> 0
            }

            if (currentDirection != 0 && lastLateralDirection != 0 && currentDirection != lastLateralDirection) {
                turnCount++
            }
            lastLateralDirection = currentDirection
        }

        fun computeMetrics(): TestMetrics {
            val durationSeconds = ((System.currentTimeMillis() - startTime) / 1000).toInt()

            return TestMetrics(
                peakLateralG = peakLateral,
                peakAccelerationG = peakAcceleration,
                peakBrakingG = peakBraking,
                peakVerticalG = peakVertical,
                lateralVariation = calculateStdDev(lateralGValues),
                longitudinalVariation = calculateStdDev(longitudinalGValues),
                verticalVariation = calculateStdDev(verticalGValues),
                turnCount = turnCount,
                durationSeconds = durationSeconds,
                dataPoints = lateralGValues.size
            )
        }

        private fun calculateStdDev(values: List<Float>): Float {
            if (values.isEmpty()) return 0f
            val mean = values.average().toFloat()
            val variance = values.map { (it - mean) * (it - mean) }.average().toFloat()
            return sqrt(variance)
        }
    }
}
