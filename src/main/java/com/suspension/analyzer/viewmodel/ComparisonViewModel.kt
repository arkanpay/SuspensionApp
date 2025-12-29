package com.suspension.analyzer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.LineData
import com.suspension.analyzer.data.SuspensionRepository
import com.suspension.analyzer.data.TestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ComparisonViewModel(private val repository: SuspensionRepository) : ViewModel() {
    private val _testResults = MutableLiveData<List<TestResult>>()
    val testResults: LiveData<List<TestResult>> = _testResults
    private val selectedTests = mutableSetOf<TestResult>()
    private val _comparisonData = MutableLiveData<ComparisonData?>()
    val comparisonData: LiveData<ComparisonData?> = _comparisonData

    fun loadTests() {
        viewModelScope.launch(Dispatchers.IO) {
            val all = repository.getAllTestResults()
            _testResults.postValue(all)
        }
    }

    fun toggleSelection(test: TestResult, selected: Boolean) {
        if (selected) selectedTests.add(test) else selectedTests.remove(test)
        if (selectedTests.size >= 2) compareSelected()
    }

    private fun compareSelected() {
        // TODO: Implement actual comparison logic and chart data
        val tests = selectedTests.toList()
        val tableText = buildTableText(tests)
        val chartData = LineData() // Placeholder
        val relativeScoreText = computeRelativeScoreText(tests)
        _comparisonData.postValue(ComparisonData(tableText, chartData, relativeScoreText))
    }

    private fun buildTableText(tests: List<TestResult>): String {
        // TODO: Build a side-by-side table of metrics
        return tests.joinToString("\n") { "${it.name}: Score ${it.score}" }
    }

    private fun computeRelativeScoreText(tests: List<TestResult>): String {
        if (tests.size < 2) return ""
        val best = tests.maxByOrNull { it.score } ?: return ""
        val worst = tests.minByOrNull { it.score } ?: return ""
        val percent = if (worst.score > 0) ((best.score - worst.score) / worst.score * 100).toInt() else 0
        return "${best.name} is $percent% better than ${worst.name}"
    }

    data class ComparisonData(
        val tableText: String,
        val chartData: LineData,
        val relativeScoreText: String
    )
}
