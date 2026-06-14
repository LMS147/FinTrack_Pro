package com.example.fintrackpro.ui.budgets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fintrackpro.data.entity.BudgetEntity
import com.example.fintrackpro.databinding.ItemRecentTransactionBinding // Reuse binding for simplicity or create new
import com.example.fintrackpro.utils.FormatUtils

class BudgetAdapter : ListAdapter<BudgetEntity, BudgetAdapter.ViewHolder>(BudgetDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecentTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemRecentTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(budget: BudgetEntity) {
            binding.tvTitle.text = budget.categoryName
            binding.tvDate.text = "${FormatUtils.formatDate(budget.startDate)} - ${FormatUtils.formatDate(budget.endDate)}"
            binding.tvAmount.text = FormatUtils.formatCurrency(budget.amount)
        }
    }

    class BudgetDiffCallback : DiffUtil.ItemCallback<BudgetEntity>() {
        override fun areItemsTheSame(oldItem: BudgetEntity, newItem: BudgetEntity): Boolean = oldItem.budgetId == newItem.budgetId
        override fun areContentsTheSame(oldItem: BudgetEntity, newItem: BudgetEntity): Boolean = oldItem == newItem
    }
}
