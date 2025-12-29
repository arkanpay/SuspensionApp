package com.suspension.analyzer.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.suspension.analyzer.databinding.FragmentDataManagerBinding
import com.suspension.analyzer.viewmodel.DataManagerViewModel
import com.suspension.analyzer.data.TestResult
import com.suspension.analyzer.ui.adapter.TestResultAdapter
import java.io.OutputStream

class DataManagerFragment : Fragment() {
    private var _binding: FragmentDataManagerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DataManagerViewModel by viewModels()
    private lateinit var adapter: TestResultAdapter

    private val exportLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri: Uri? ->
        uri?.let { viewModel.exportSelectedTests(requireContext(), it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDataManagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TestResultAdapter(
            onEdit = { test -> showEditDialog(test) },
            onDelete = { test -> viewModel.deleteTest(test) },
            onSelect = { test, selected -> viewModel.toggleSelection(test, selected) }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        binding.exportButton.setOnClickListener { exportLauncher.launch("suspension_tests.json") }
        binding.importButton.setOnClickListener { importTests() }
        binding.searchEdit.addTextChangedListener { text -> viewModel.filter(text.toString()) }
        viewModel.testResults.observe(viewLifecycleOwner) { adapter.submitList(it) }
        viewModel.loadTests()
    }

    private fun showEditDialog(test: TestResult) {
        val editText = EditText(requireContext()).apply { setText(test.name) }
        AlertDialog.Builder(requireContext())
            .setTitle("Edit Test Name")
            .setView(editText)
            .setPositiveButton("Save") { _, _ -> viewModel.editTestName(test, editText.text.toString()) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun importTests() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        }
        startActivityForResult(intent, 1001)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
