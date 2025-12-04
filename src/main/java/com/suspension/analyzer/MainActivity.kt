package com.suspension.analyzer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.suspension.analyzer.data.SuspensionAnalyzerDatabase
import com.suspension.analyzer.data.SuspensionRepository
import com.suspension.analyzer.sensors.SensorDataManager
import com.suspension.analyzer.ui.CarVisualization
import kotlinx.coroutines.launch
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorDataManager
    private lateinit var carVisualization: CarVisualization
    private lateinit var repository: SuspensionRepository

    // UI Elements
    private lateinit var gForceTextView: TextView
    private lateinit var lateralGTextView: TextView
    private lateinit var longitudinalGTextView: TextView
    private lateinit var verticalGTextView: TextView
    private lateinit var peakGTextView: TextView
    private lateinit var startButton: Button
    private lateinit var calibrateButton: Button
    private lateinit var statusTextView: TextView

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            initializeSensors()
        } else {
            Toast.makeText(this, "Sensor permissions required", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize database
        val database = SuspensionAnalyzerDatabase.getInstance(this)
        repository = SuspensionRepository(database)

        // Initialize UI
        initializeUI()

        // Request permissions
        checkAndRequestPermissions()
    }

    private fun initializeUI() {
        carVisualization = findViewById(R.id.carVisualization)
        gForceTextView = findViewById(R.id.gForceTextView)
        lateralGTextView = findViewById(R.id.lateralGTextView)
        longitudinalGTextView = findViewById(R.id.longitudinalGTextView)
        verticalGTextView = findViewById(R.id.verticalGTextView)
        peakGTextView = findViewById(R.id.peakGTextView)
        startButton = findViewById(R.id.startButton)
        calibrateButton = findViewById(R.id.calibrateButton)
        statusTextView = findViewById(R.id.statusTextView)

        startButton.text = "Start Test"
        startButton.setOnClickListener {
            // Launch test selection screen
            val intent = Intent(this, TestSelectionActivity::class.java)
            startActivity(intent)
        }

        calibrateButton.setOnClickListener {
            calibrateSensors()
        }
    }

    private fun checkAndRequestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA
        )

        val permissionsNeeded = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsNeeded.isEmpty()) {
            initializeSensors()
        } else {
            permissionLauncher.launch(permissionsNeeded.toTypedArray())
        }
    }

    private fun initializeSensors() {
        try {
            sensorManager = SensorDataManager(this)

            sensorManager.onDataUpdated = { reading ->
                runOnUiThread {
                    try {
                        updateUI(reading)
                    } catch (e: Exception) {
                        // Ignore UI update errors
                    }
                }
            }

            sensorManager.startListening()
            statusTextView.text = "Sensors Active"
        } catch (e: Exception) {
            statusTextView.text = "Sensor Error: ${e.message}"
            Toast.makeText(this, "Sensor initialization failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
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

        // Update individual G-force components
        lateralGTextView.text = String.format("Lateral: %.2f G", reading.peakLateralG)
        longitudinalGTextView.text = String.format("Longitudinal: %.2f G", reading.peakLongitudinalG)
        verticalGTextView.text = String.format("Vertical: %.2f G", reading.peakVerticalG)

        // Show live peak values
        peakGTextView.text = String.format(
            "Peak: L:%.2f | Lo:%.2f | V:%.2f",
            abs(reading.peakLateralG),
            abs(reading.peakLongitudinalG),
            abs(reading.peakVerticalG)
        )

        // Color code based on G-force intensity
        val color = when {
            totalG > 1.5f -> android.graphics.Color.RED
            totalG > 1.0f -> android.graphics.Color.YELLOW
            else -> android.graphics.Color.GREEN
        }
        gForceTextView.setTextColor(color)
    }

    private fun calibrateSensors() {
        statusTextView.text = "Calibrating... Keep phone still!"
        statusTextView.setTextColor(android.graphics.Color.YELLOW)
        calibrateButton.isEnabled = false

        lifecycleScope.launch {
            try {
                // Run calibration in background
                kotlinx.coroutines.Dispatchers.IO.run {
                    sensorManager.calibrate(100)
                }

                runOnUiThread {
                    statusTextView.text = "Calibration Complete"
                    statusTextView.setTextColor(android.graphics.Color.GREEN)
                    calibrateButton.isEnabled = true
                    Toast.makeText(this@MainActivity, "Sensors calibrated!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    statusTextView.text = "Calibration Failed"
                    statusTextView.setTextColor(android.graphics.Color.RED)
                    calibrateButton.isEnabled = true
                    Toast.makeText(this@MainActivity, "Calibration error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::sensorManager.isInitialized) {
            sensorManager.stopListening()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::sensorManager.isInitialized) {
            sensorManager.stopListening()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::sensorManager.isInitialized) {
            sensorManager.startListening()
        }
    }
}
