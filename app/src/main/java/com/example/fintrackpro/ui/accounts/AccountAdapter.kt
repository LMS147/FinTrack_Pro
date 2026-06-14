package com.example.fintrackpro.ui.accounts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fintrackpro.data.entity.AccountEntity
import com.example.fintrackpro.databinding.ItemRecentTransactionBinding
import com.example.fintrackpro.utils.FormatUtils

class AccountAdapter : ListAdapter<AccountEntity, AccountAdapter.ViewHolder>(AccountDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecentTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemRecentTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(account: AccountEntity) {
            binding.tvTitle.text = account.accountName
            binding.tvDate.text = account.accountType
            binding.tvAmount.text = FormatUtils.formatCurrency(account.balance, account.currency)
        }
    }

    class AccountDiffCallback : DiffUtil.ItemCallback<AccountEntity>() {
        override fun areItemsTheSame(oldItem: AccountEntity, newItem: AccountEntity): Boolean = oldItem.accountId == newItem.accountId
        override fun areContentsTheSame(oldItem: AccountEntity, newItem: AccountEntity): Boolean = oldItem == newItem
    }
}
