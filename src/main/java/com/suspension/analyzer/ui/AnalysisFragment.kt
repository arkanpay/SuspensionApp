package com.suspension.analyzer.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.suspension.analyzer.databinding.FragmentAnalysisBinding
import com.suspension.analyzer.analysis.SuspensionAnalyzer
import com.suspension.analyzer.viewmodel.AnalysisViewModel
import org.json.JSONArray
import org.json.JSONObject

class AnalysisFragment : Fragment() {
    private var _binding: FragmentAnalysisBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AnalysisViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.metricsLiveData.observe(viewLifecycleOwner) { metricsList ->
            setupCharts(metricsList)
        }
        viewModel.loadMetrics() // Loads metrics for the latest test or selected test
    }

    private fun setupCharts(metricsList: List<SuspensionAnalyzer.Metrics>) {
        setupLineChart(binding.lineChart, viewModel.timeSeriesData)
        setupBarChart(binding.barChart, metricsList)
        setupScatterChart(binding.scatterChart, metricsList)
    }

    private fun setupLineChart(chart: LineChart, timeSeries: List<Pair<Float, Float>>) {
        val entries = timeSeries.map { Entry(it.first, it.second) }
        val dataSet = LineDataSet(entries, "Acceleration vs Time").apply {
            color = ColorTemplate.getHoloBlue()
            setDrawCircles(false)
            lineWidth = 2f
            setDrawValues(false)
        }
        chart.data = LineData(dataSet)
        chart.description = Description().apply { text = "Acceleration (Z) over Time" }
        chart.setTouchEnabled(true)
        chart.setPinchZoom(true)
        chart.invalidate()
    }

    private fun setupBarChart(chart: BarChart, metricsList: List<SuspensionAnalyzer.Metrics>) {
        val rmsEntries = metricsList.mapIndexed { i, m -> BarEntry(i.toFloat(), m.rmsVertical.toFloat()) }
        val peakEntries = metricsList.mapIndexed { i, m -> BarEntry(i.toFloat(), m.peakVertical.toFloat()) }
        val freqEntries = metricsList.mapIndexed { i, m -> BarEntry(i.toFloat(), m.vibrationFrequency.toFloat()) }
        val rmsSet = BarDataSet(rmsEntries, "RMS").apply { color = Color.BLUE }
        val peakSet = BarDataSet(peakEntries, "Peak").apply { color = Color.RED }
        val freqSet = BarDataSet(freqEntries, "Frequency").apply { color = Color.GREEN }
        val data = BarData(rmsSet, peakSet, freqSet)
        chart.data = data
        chart.description = Description().apply { text = "Metrics per Speed Bucket" }
        chart.setTouchEnabled(true)
        chart.setPinchZoom(true)
        chart.invalidate()
    }

    private fun setupScatterChart(chart: ScatterChart, metricsList: List<SuspensionAnalyzer.Metrics>) {
        val entries = metricsList.map { Entry(it.speedBucket.toFloat(), it.vibrationFrequency.toFloat()) }
        val dataSet = ScatterDataSet(entries, "Frequency vs Speed").apply { color = Color.MAGENTA }
        chart.data = ScatterData(dataSet)
        chart.description = Description().apply { text = "Frequency vs Speed Bucket" }
        chart.setTouchEnabled(true)
        chart.setPinchZoom(true)
        chart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
