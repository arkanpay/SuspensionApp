package com.suspension.analyzer

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileWriter

class TestResultsActivity : AppCompatActivity() {

    private var testName = ""
    private var overallScore = 0
    private var rating = ""
    private var accelerationScore = 0
    private var brakingScore = 0
    private var corneringScore = 0
    private var smoothnessScore = 0
    private lateinit var breakdownKeys: Array<String>
    private lateinit var breakdownValues: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_results)

        // Get data from intent
        testName = intent.getStringExtra("TEST_NAME") ?: "Test"
        overallScore = intent.getIntExtra("OVERALL_SCORE", 0)
        rating = intent.getStringExtra("RATING") ?: "N/A"
        accelerationScore = intent.getIntExtra("ACCELERATION_SCORE", 0)
        brakingScore = intent.getIntExtra("BRAKING_SCORE", 0)
        corneringScore = intent.getIntExtra("CORNERING_SCORE", 0)
        smoothnessScore = intent.getIntExtra("SMOOTHNESS_SCORE", 0)
        breakdownKeys = intent.getStringArrayExtra("BREAKDOWN_KEYS") ?: arrayOf()
        breakdownValues = intent.getStringArrayExtra("BREAKDOWN_VALUES") ?: arrayOf()

        displayResults()
        setupButtons()
    }

    private fun displayResults() {
        // Set test name
        findViewById<TextView>(R.id.testNameTextView).text = testName

        // Set overall score
        val scoreTextView = findViewById<TextView>(R.id.overallScoreTextView)
        scoreTextView.text = overallScore.toString()

        // Set rating with color
        val ratingTextView = findViewById<TextView>(R.id.ratingTextView)
        ratingTextView.text = rating
        ratingTextView.setTextColor(getRatingColor(rating))
        scoreTextView.setTextColor(getRatingColor(rating))

        // Add score breakdown
        val scoreDetailsContainer = findViewById<LinearLayout>(R.id.scoreDetailsContainer)
        if (accelerationScore > 0) {
            addScoreCard(scoreDetailsContainer, "Acceleration", accelerationScore)
        }
        if (brakingScore > 0) {
            addScoreCard(scoreDetailsContainer, "Braking", brakingScore)
        }
        if (corneringScore > 0) {
            addScoreCard(scoreDetailsContainer, "Cornering", corneringScore)
        }
        addScoreCard(scoreDetailsContainer, "Smoothness", smoothnessScore)

        // Add metrics breakdown
        val metricsContainer = findViewById<LinearLayout>(R.id.metricsContainer)
        breakdownKeys.forEachIndexed { index, key ->
            if (index < breakdownValues.size) {
                addMetricRow(metricsContainer, key, breakdownValues[index])
            }
        }
    }

    private fun addScoreCard(container: LinearLayout, label: String, score: Int) {
        val card = CardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16)
            }
            setCardBackgroundColor(Color.parseColor("#2a2a2a"))
            radius = 8f
            cardElevation = 4f
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(24, 16, 24, 16)
        }

        val labelTextView = TextView(this).apply {
            text = label
            textSize = 16f
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val scoreTextView = TextView(this).apply {
            text = score.toString()
            textSize = 24f
            setTextColor(getScoreColor(score))
            gravity = Gravity.END
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        layout.addView(labelTextView)
        layout.addView(scoreTextView)
        card.addView(layout)
        container.addView(card)
    }

    private fun addMetricRow(container: LinearLayout, label: String, value: String) {
        val card = CardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 8)
            }
            setCardBackgroundColor(Color.parseColor("#222222"))
            radius = 4f
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(16, 12, 16, 12)
        }

        val labelTextView = TextView(this).apply {
            text = label
            textSize = 14f
            setTextColor(Color.parseColor("#AAAAAA"))
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val valueTextView = TextView(this).apply {
            text = value
            textSize = 14f
            setTextColor(Color.WHITE)
            gravity = Gravity.END
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        layout.addView(labelTextView)
        layout.addView(valueTextView)
        card.addView(layout)
        container.addView(card)
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.shareButton).setOnClickListener {
            shareResults()
        }

        findViewById<Button>(R.id.newTestButton).setOnClickListener {
            val intent = Intent(this, TestSelectionActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun shareResults() {
        try {
            // Create text report
            val report = buildString {
                appendLine("SUSPENSION TEST RESULTS")
                appendLine("=" .repeat(40))
                appendLine()
                appendLine("Test: $testName")
                appendLine("Date: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}")
                appendLine()
                appendLine("OVERALL SCORE: $overallScore/100")
                appendLine("Rating: $rating")
                appendLine()
                appendLine("PERFORMANCE BREAKDOWN")
                appendLine("-" .repeat(40))
                if (accelerationScore > 0) appendLine("Acceleration:  $accelerationScore/100")
                if (brakingScore > 0) appendLine("Braking:       $brakingScore/100")
                if (corneringScore > 0) appendLine("Cornering:     $corneringScore/100")
                appendLine("Smoothness:    $smoothnessScore/100")
                appendLine()
                appendLine("TEST METRICS")
                appendLine("-" .repeat(40))
                breakdownKeys.forEachIndexed { index, key ->
                    if (index < breakdownValues.size) {
                        appendLine("$key: ${breakdownValues[index]}")
                    }
                }
                appendLine()
                appendLine("Generated by Suspension Analyzer App")
            }

            // Save to file
            val file = File(cacheDir, "suspension_test_results.txt")
            FileWriter(file).use { it.write(report) }

            // Share using system share dialog
            val uri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Suspension Test Results - $testName")
                putExtra(Intent.EXTRA_TEXT, report)
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(shareIntent, "Share Test Results"))
        } catch (e: Exception) {
            Toast.makeText(this, "Error sharing results: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getRatingColor(rating: String): Int = when (rating) {
        "Excellent" -> Color.parseColor("#4CAF50")
        "Good" -> Color.parseColor("#8BC34A")
        "Average" -> Color.parseColor("#FFC107")
        "Fair" -> Color.parseColor("#FF9800")
        "Poor" -> Color.parseColor("#F44336")
        else -> Color.WHITE
    }

    private fun getScoreColor(score: Int): Int = when {
        score >= 90 -> Color.parseColor("#4CAF50")
        score >= 75 -> Color.parseColor("#8BC34A")
        score >= 60 -> Color.parseColor("#FFC107")
        score >= 40 -> Color.parseColor("#FF9800")
        else -> Color.parseColor("#F44336")
    }
}
