package com.suspension.analyzer.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suspension.analyzer.data.SuspensionRepository
import com.suspension.analyzer.data.TestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.OutputStream

class DataManagerViewModel(private val repository: SuspensionRepository) : ViewModel() {
    private val _testResults = MutableLiveData<List<TestResult>>()
    val testResults: LiveData<List<TestResult>> = _testResults
    private val selectedTests = mutableSetOf<TestResult>()
    private var allTests: List<TestResult> = emptyList()

    fun loadTests() {
        viewModelScope.launch(Dispatchers.IO) {
            allTests = repository.getAllTestResults()
            _testResults.postValue(allTests)
        }
    }

    fun filter(query: String) {
        _testResults.value = if (query.isBlank()) allTests else allTests.filter { it.name.contains(query, true) }
    }

    fun editTestName(test: TestResult, newName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = test.copy(name = newName)
            repository.updateTestResult(updated)
            loadTests()
        }
    }

    fun deleteTest(test: TestResult) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTestResult(test)
            loadTests()
        }
    }

    fun toggleSelection(test: TestResult, selected: Boolean) {
        if (selected) selectedTests.add(test) else selectedTests.remove(test)
    }

    fun exportSelectedTests(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val json = JSONArray(selectedTests.map { itToJson(it) }).toString()
            context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray()) }
        }
    }

    private fun itToJson(test: TestResult): String = "" // TODO: Implement JSON serialization
}
