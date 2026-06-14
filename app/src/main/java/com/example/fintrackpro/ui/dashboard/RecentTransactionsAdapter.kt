package com.example.fintrackpro.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.fintrackpro.R
import com.example.fintrackpro.data.entity.TransactionEntity
import com.example.fintrackpro.databinding.ItemTransactionBinding
import com.example.fintrackpro.utils.FormatUtils

class RecentTransactionsAdapter(
    private val transactions: List<TransactionEntity>,
    private val currency: String
) : RecyclerView.Adapter<RecentTransactionsAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: TransactionEntity, currency: String) {
            binding.tvDescription.text = transaction.title
            binding.tvCategory.text = transaction.categoryId
            binding.tvAmount.text = FormatUtils.formatCurrency(transaction.amount, currency)
            binding.tvDate.text = FormatUtils.formatDate(transaction.date)

            val colorRes = if (transaction.type == "INCOME") R.color.primary_green else R.color.error_red
            binding.tvAmount.setTextColor(ContextCompat.getColor(binding.root.context, colorRes))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position], currency)
    }

    override fun getItemCount(): Int = transactions.size
}
