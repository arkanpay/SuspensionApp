package com.suspension.analyzer.analysis

import com.suspension.analyzer.sensors.SensorDataManager
import org.junit.Assert.assertEquals
import org.junit.Test

class SuspensionAnalyzerTest {
    @Test
    fun testRMSCalculation() {
        val readings = listOf(
            SensorDataManager.SensorReading(0,0f,0f,1f,1f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f),
            SensorDataManager.SensorReading(1,0f,0f,2f,2f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f),
            SensorDataManager.SensorReading(2,0f,0f,3f,3f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f)
        )
        val metrics = SuspensionAnalyzer.analyze(readings)
        val expectedRMS = Math.sqrt((1.0*1 + 2.0*2 + 3.0*3)/3)
        assertEquals(expectedRMS, metrics[0].rmsVertical, 0.01)
    }

    @Test
    fun testPeakCalculation() {
        val readings = listOf(
            SensorDataManager.SensorReading(0,0f,0f,1f,1f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f),
            SensorDataManager.SensorReading(1,0f,0f,5f,5f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f),
            SensorDataManager.SensorReading(2,0f,0f,-2f,-2f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f)
        )
        val metrics = SuspensionAnalyzer.analyze(readings)
        assertEquals(5.0, metrics[0].peakVertical, 0.01)
    }

    @Test
    fun testFrequencyCalculation() {
        // Simulate 3 peaks in 2 seconds
        val readings = listOf(
            SensorDataManager.SensorReading(0,0f,0f,1f,1f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f),
            SensorDataManager.SensorReading(1000,0f,0f,-1f,-1f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f),
            SensorDataManager.SensorReading(2000,0f,0f,1f,1f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f)
        )
        val metrics = SuspensionAnalyzer.analyze(readings)
        // Should be about 1.0 Hz
        assertEquals(1.0, metrics[0].vibrationFrequency, 0.2)
    }
}
