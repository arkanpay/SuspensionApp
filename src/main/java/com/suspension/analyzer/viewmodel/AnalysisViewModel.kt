package com.suspension.analyzer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suspension.analyzer.analysis.SuspensionAnalyzer
import com.suspension.analyzer.data.SuspensionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class AnalysisViewModel(private val repository: SuspensionRepository) : ViewModel() {
    private val _metricsLiveData = MutableLiveData<List<SuspensionAnalyzer.Metrics>>()
    val metricsLiveData: LiveData<List<SuspensionAnalyzer.Metrics>> = _metricsLiveData

    // For line chart: time (s) vs filteredAccelZ
    var timeSeriesData: List<Pair<Float, Float>> = emptyList()
        private set

    fun loadMetrics(testResultId: Int? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val testResult = if (testResultId != null) {
                repository.getTestResult(testResultId)
            } else {
                repository.getAllTestResults().firstOrNull()
            }
            if (testResult != null) {
                val rawData = parseRawData(testResult.rawDataJson)
                val metrics = SuspensionAnalyzer.analyze(rawData)
                timeSeriesData = rawData.map { Pair((it.timestamp - rawData.first().timestamp) / 1000f, it.filteredAccelZ) }
                _metricsLiveData.postValue(metrics)
            }
        }
    }

    private fun parseRawData(json: String): List<com.suspension.analyzer.sensors.SensorDataManager.SensorReading> {
        // TODO: Implement JSON parsing to SensorReading list
        return emptyList()
    }
}
