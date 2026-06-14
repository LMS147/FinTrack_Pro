package com.example.fintrackpro.ui.expense

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fintrackpro.data.entity.TransactionEntity
import com.example.fintrackpro.databinding.ItemExpenseBinding
import com.example.fintrackpro.utils.FormatUtils

class ExpenseAdapter(
    private val currencyCode: String = "ZAR",
    private val onItemClick: (TransactionEntity) -> Unit
) : ListAdapter<TransactionEntity, ExpenseAdapter.ExpenseViewHolder>(DiffCallback) {

    inner class ExpenseViewHolder(private val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: TransactionEntity) {
            binding.tvDescription.text = expense.title
            binding.tvAmount.text = FormatUtils.formatCurrency(expense.amount, currencyCode)
            binding.tvDate.text = FormatUtils.formatDate(expense.date)
            binding.tvCategory.text = expense.categoryId 
            binding.ivPhoto.visibility = android.view.View.GONE 

            val color = if (expense.type == "INCOME")
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

    companion object DiffCallback : DiffUtil.ItemCallback<TransactionEntity>() {
        override fun areItemsTheSame(oldItem: TransactionEntity, newItem: TransactionEntity): Boolean {
            return oldItem.transactionId == newItem.transactionId
        }

        override fun areContentsTheSame(oldItem: TransactionEntity, newItem: TransactionEntity): Boolean {
            return oldItem == newItem
        }
    }

    fun getExpenseAt(position: Int): TransactionEntity = getItem(position)
}
