package com.suspension.analyzer

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.suspension.analyzer.sensors.SensorDataManager
import com.suspension.analyzer.test.TestProtocolManager
import com.suspension.analyzer.ui.CarVisualization

class TestExecutionActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorDataManager
    private lateinit var carVisualization: CarVisualization
    private lateinit var testAnalyzer: TestProtocolManager.TestAnalyzer

    private lateinit var testNameTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var gForceTextView: TextView
    private lateinit var lateralGTextView: TextView
    private lateinit var longitudinalGTextView: TextView
    private lateinit var verticalGTextView: TextView
    private lateinit var stopTestButton: Button

    private var testName = ""
    private var testDuration = 30
    private var timer: CountDownTimer? = null
    private var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // Reuse main layout for now

        testName = intent.getStringExtra("TEST_NAME") ?: "Test"
        testDuration = intent.getIntExtra("TEST_DURATION", 30)

        initializeUI()
        initializeSensors()
        startTest()
    }

    private fun initializeUI() {
        carVisualization = findViewById(R.id.carVisualization)
        testNameTextView = findViewById<TextView>(R.id.statusTextView)
        timerTextView = testNameTextView  // Reuse for timer
        gForceTextView = findViewById(R.id.gForceTextView)
        lateralGTextView = findViewById(R.id.lateralGTextView)
        longitudinalGTextView = findViewById(R.id.longitudinalGTextView)
        verticalGTextView = findViewById(R.id.verticalGTextView)
        stopTestButton = findViewById<Button>(R.id.startButton)

        stopTestButton.text = "Stop Test"
        stopTestButton.setBackgroundColor(android.graphics.Color.RED)
        stopTestButton.setOnClickListener {
            finishTest()
        }
    }

    private fun initializeSensors() {
        sensorManager = SensorDataManager(this)
        testAnalyzer = TestProtocolManager.TestAnalyzer()

        sensorManager.onDataUpdated = { reading ->
            runOnUiThread {
                updateUI(reading)
                if (isRecording) {
                    testAnalyzer.processSensorReading(reading)
                }
            }
        }

        sensorManager.startListening()
    }

    private fun startTest() {
        isRecording = true
        testAnalyzer.startAnalysis()

        testNameTextView.text = "Test: $testName"
        testNameTextView.setTextColor(android.graphics.Color.RED)

        Toast.makeText(this, "Test Started! Drive safely.", Toast.LENGTH_LONG).show()

        // Start countdown timer
        timer = object : CountDownTimer((testDuration * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = (millisUntilFinished / 1000).toInt()
                testNameTextView.text = "$testName - ${secondsRemaining}s remaining"
            }

            override fun onFinish() {
                finishTest()
            }
        }.start()
    }

    private fun finishTest() {
        isRecording = false
        timer?.cancel()

        Toast.makeText(this, "Test Complete! Analyzing...", Toast.LENGTH_SHORT).show()

        // Compute metrics and score
        val metrics = testAnalyzer.computeMetrics()

        // Determine which test protocol was used
        val protocol = when (testName) {
            "Standing Start/Stop" -> TestProtocolManager.StandingStartStopTest()
            "Figure-8" -> TestProtocolManager.Figure8Test()
            else -> TestProtocolManager.StandingStartStopTest()
        }

        val score = protocol.calculateScore(metrics)

        // Navigate to results
        val intent = Intent(this, TestResultsActivity::class.java)
        intent.putExtra("TEST_NAME", testName)
        intent.putExtra("OVERALL_SCORE", score.overallScore)
        intent.putExtra("RATING", score.rating)
        intent.putExtra("ACCELERATION_SCORE", score.accelerationScore ?: 0)
        intent.putExtra("BRAKING_SCORE", score.brakingScore ?: 0)
        intent.putExtra("CORNERING_SCORE", score.corneringScore ?: 0)
        intent.putExtra("SMOOTHNESS_SCORE", score.smoothnessScore)

        // Pass breakdown as array
        val breakdownKeys = score.breakdown.keys.toTypedArray()
        val breakdownValues = score.breakdown.values.toTypedArray()
        intent.putExtra("BREAKDOWN_KEYS", breakdownKeys)
        intent.putExtra("BREAKDOWN_VALUES", breakdownValues)

        startActivity(intent)
        finish()
    }

    private fun updateUI(reading: SensorDataManager.SensorReading) {
        // Update 3D car visualization
        carVisualization.updateOrientation(reading.pitch, reading.roll, reading.yaw)

        // Calculate total G-force magnitude
        val totalG = kotlin.math.sqrt(
            reading.accelX * reading.accelX +
                    reading.accelY * reading.accelY +
                    reading.accelZ * reading.accelZ
        ) / 9.81f

        gForceTextView.text = String.format("Total G-Force: %.2f G", totalG)
        lateralGTextView.text = String.format("Lateral: %.2f G", reading.peakLateralG)
        longitudinalGTextView.text = String.format("Longitudinal: %.2f G", reading.peakLongitudinalG)
        verticalGTextView.text = String.format("Vertical: %.2f G", reading.peakVerticalG)

        // Color code based on G-force intensity
        val color = when {
            totalG > 1.5f -> android.graphics.Color.RED
            totalG > 1.0f -> android.graphics.Color.YELLOW
            else -> android.graphics.Color.GREEN
        }
        gForceTextView.setTextColor(color)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        if (::sensorManager.isInitialized) {
            sensorManager.stopListening()
        }
    }
}
