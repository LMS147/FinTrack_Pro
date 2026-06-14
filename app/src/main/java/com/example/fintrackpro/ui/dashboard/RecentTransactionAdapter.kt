package com.example.fintrackpro.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fintrackpro.R
import com.example.fintrackpro.data.entity.TransactionEntity
import com.example.fintrackpro.databinding.ItemRecentTransactionBinding
import com.example.fintrackpro.utils.FormatUtils

class RecentTransactionAdapter : ListAdapter<TransactionEntity, RecentTransactionAdapter.ViewHolder>(
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

        fun bind(transaction: TransactionEntity) {
            binding.tvTitle.text = transaction.title
            binding.tvDate.text = FormatUtils.formatDate(transaction.date)
            binding.tvAmount.text = FormatUtils.formatCurrency(transaction.amount)

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

    private class TransactionDiffCallback : DiffUtil.ItemCallback<TransactionEntity>() {
        override fun areItemsTheSame(oldItem: TransactionEntity, newItem: TransactionEntity): Boolean {
            return oldItem.transactionId == newItem.transactionId
        }

        override fun areContentsTheSame(oldItem: TransactionEntity, newItem: TransactionEntity): Boolean {
            return oldItem == newItem
        }
    }
}
