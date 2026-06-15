package com.example.fintrackpro.ui.shared

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fintrackpro.data.entity.SharedWalletEntity
import com.example.fintrackpro.databinding.ItemSharedWalletBinding
import java.text.NumberFormat
import java.util.*

class SharedWalletAdapter(private val onInviteClick: (String) -> Unit) : 
    ListAdapter<SharedWalletEntity, SharedWalletAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSharedWalletBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemSharedWalletBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(wallet: SharedWalletEntity) {
            binding.tvWalletName.text = wallet.name
            binding.tvDescription.text = wallet.description ?: "No description"
            
            val formatter = NumberFormat.getCurrencyInstance(Locale.US)
            binding.tvBalance.text = formatter.format(wallet.totalBalance)
            
            binding.tvInviteCode.text = "Code: ${wallet.inviteCode}"
            binding.tvInviteCode.setOnClickListener { onInviteClick(wallet.inviteCode) }
            
            val members = wallet.memberIds.split(",").filter { it.isNotEmpty() }
            binding.tvMemberCount.text = "${members.size} Members"
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<SharedWalletEntity>() {
        override fun areItemsTheSame(oldItem: SharedWalletEntity, newItem: SharedWalletEntity): Boolean {
            return oldItem.walletId == newItem.walletId
        }

        override fun areContentsTheSame(oldItem: SharedWalletEntity, newItem: SharedWalletEntity): Boolean {
            return oldItem == newItem
        }
    }
}
