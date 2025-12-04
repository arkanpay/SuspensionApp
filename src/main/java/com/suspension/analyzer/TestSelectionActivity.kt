package com.suspension.analyzer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.suspension.analyzer.test.TestProtocolManager

class TestSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_selection)

        val startStopButton = findViewById<Button>(R.id.startStopButton)
        val figure8Button = findViewById<Button>(R.id.figure8Button)

        startStopButton.setOnClickListener {
            startTest(TestProtocolManager.StandingStartStopTest())
        }

        figure8Button.setOnClickListener {
            startTest(TestProtocolManager.Figure8Test())
        }
    }

    private fun startTest(protocol: TestProtocolManager.TestProtocol) {
        val intent = Intent(this, TestExecutionActivity::class.java)
        intent.putExtra("TEST_NAME", protocol.name)
        intent.putExtra("TEST_DESCRIPTION", protocol.description)
        intent.putExtra("TEST_DURATION", protocol.durationSeconds)
        startActivity(intent)
    }
}
