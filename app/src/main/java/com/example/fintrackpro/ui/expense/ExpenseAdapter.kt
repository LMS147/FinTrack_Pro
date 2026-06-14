package com.example.fintrackpro.ui.expense

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fintrackpro.data.entity.Transaction
import com.example.fintrackpro.databinding.ItemExpenseBinding
import com.example.fintrackpro.utils.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.Locale

class ExpenseAdapter(
    private val currencyCode: String = "ZAR",
    private val onItemClick: (Transaction) -> Unit
) : ListAdapter<Transaction, ExpenseAdapter.ExpenseViewHolder>(DiffCallback) {

    inner class ExpenseViewHolder(private val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: Transaction) {
            binding.tvDescription.text = expense.description
            binding.tvAmount.text = CurrencyFormatter.format(expense.amount, currencyCode)
            binding.tvDate.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(expense.date)
            binding.tvCategory.text = "Category " + expense.categoryId 
            binding.ivPhoto.visibility = android.view.View.GONE 

            val color = if (expense.isIncome)
                binding.root.context.getColor(com.example.fintrackpro.R.color.primary_green)
            else
                binding.root.context.getColor(com.example.fintrackpro.R.color.error_red)
            binding.tvAmount.setTextColor(color)

            binding.root.setOnClickListener { onItemClick(expense) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.expenseId == newItem.expenseId
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }

    fun getExpenseAt(position: Int): Transaction = getItem(position)
}