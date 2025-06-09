package com.example.purrse1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryBudgetAdapter(
    private val items: List<CategoryBudgetUsage>
) : RecyclerView.Adapter<CategoryBudgetAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategoryName: TextView = view.findViewById(R.id.textCategoryName)
        val progressBar: ProgressBar = view.findViewById(R.id.budgetProgressBar)
        val tvBudgetUsageText: TextView = view.findViewById(R.id.textBudgetSummary)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_budget_bar, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvCategoryName.text = item.categoryName
        holder.tvBudgetUsageText.text = "${item.usedAmount} / ${item.assignedBudget}"

        val progressPercent =
            if (item.assignedBudget > 0) (item.usedAmount / item.assignedBudget * 100).toInt() else 0
        holder.progressBar.progress = progressPercent.coerceIn(0, 100)
    }
}

