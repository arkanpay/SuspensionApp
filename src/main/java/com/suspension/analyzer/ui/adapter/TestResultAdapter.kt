package com.suspension.analyzer.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.suspension.analyzer.R
import com.suspension.analyzer.data.TestResult

class TestResultAdapter(
    val onEdit: (TestResult) -> Unit,
    val onDelete: (TestResult) -> Unit,
    val onSelect: (TestResult, Boolean) -> Unit
) : ListAdapter<TestResult, TestResultAdapter.ViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_test_result, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.testName)
        private val date: TextView = view.findViewById(R.id.testDate)
        private val score: TextView = view.findViewById(R.id.testScore)
        private val edit: ImageButton = view.findViewById(R.id.editButton)
        private val delete: ImageButton = view.findViewById(R.id.deleteButton)
        private val select: CheckBox = view.findViewById(R.id.selectBox)
        fun bind(test: TestResult) {
            name.text = test.name
            date.text = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(java.util.Date(test.date))
            score.text = "Score: %.1f".format(test.score)
            edit.setOnClickListener { onEdit(test) }
            delete.setOnClickListener { onDelete(test) }
            select.setOnCheckedChangeListener { _, isChecked -> onSelect(test, isChecked) }
        }
    }
    class DiffCallback : DiffUtil.ItemCallback<TestResult>() {
        override fun areItemsTheSame(oldItem: TestResult, newItem: TestResult) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: TestResult, newItem: TestResult) = oldItem == newItem
    }
}
