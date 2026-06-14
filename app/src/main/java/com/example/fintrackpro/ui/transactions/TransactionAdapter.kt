package com.example.fintrackpro.ui.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fintrackpro.R
import com.example.fintrackpro.data.entity.TransactionWithCategory
import com.example.fintrackpro.databinding.ItemRecentTransactionBinding
import com.example.fintrackpro.utils.FormatUtils

class TransactionAdapter : ListAdapter<TransactionWithCategory, TransactionAdapter.ViewHolder>(
    TransactionDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecentTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemRecentTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TransactionWithCategory) {
            val transaction = item.transaction
            binding.tvTitle.text = transaction.title
            binding.tvCategory.text = item.category.name
            binding.tvDate.text = FormatUtils.formatDate(transaction.date)

            val color = if (transaction.type == "INCOME") {
                ContextCompat.getColor(binding.root.context, R.color.primary_green)
            } else {
                ContextCompat.getColor(binding.root.context, R.color.error_red)
            }
            binding.tvAmount.setTextColor(color)

            val prefix = if (transaction.type == "INCOME") "+" else "-"
            binding.tvAmount.text = "$prefix ${FormatUtils.formatCurrency(transaction.amount)}"
        }
    }

    private class TransactionDiffCallback : DiffUtil.ItemCallback<TransactionWithCategory>() {
        override fun areItemsTheSame(oldItem: TransactionWithCategory, newItem: TransactionWithCategory): Boolean {
            return oldItem.transaction.transactionId == newItem.transaction.transactionId
        }

        override fun areContentsTheSame(oldItem: TransactionWithCategory, newItem: TransactionWithCategory): Boolean {
            return oldItem == newItem
        }
    }
}
