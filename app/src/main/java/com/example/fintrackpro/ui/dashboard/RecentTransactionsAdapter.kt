package com.example.fintrackpro.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fintrackpro.data.entity.Transaction
import com.example.fintrackpro.databinding.ItemTransactionBinding
import com.example.fintrackpro.utils.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.Locale

class RecentTransactionsAdapter(
    private val transactions: List<Transaction>,
    private val currencyCode: String = "ZAR"
) : RecyclerView.Adapter<RecentTransactionsAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(expense: Transaction) {
            binding.tvDescription.text = expense.description
            binding.tvCategory.text = expense.categoryId.toString() // Replace with category name if joined
            binding.tvAmount.text = CurrencyFormatter.format(expense.amount, currencyCode)
            binding.tvDate.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(expense.date)

            // Income/Transaction color
            val color = if (expense.isIncome)
                binding.root.context.getColor(com.example.fintrackpro.R.color.primary_green)
            else
                binding.root.context.getColor(com.example.fintrackpro.R.color.error_red)
            binding.tvAmount.setTextColor(color)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount(): Int = transactions.size
}