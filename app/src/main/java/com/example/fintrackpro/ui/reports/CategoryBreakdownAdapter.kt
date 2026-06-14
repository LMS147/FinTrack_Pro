package com.example.fintrackpro.ui.reports

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fintrackpro.data.entity.CategorySpendingSummary
import com.example.fintrackpro.databinding.ItemCategoryBreakdownBinding
import com.example.fintrackpro.utils.CurrencyFormatter

class CategoryBreakdownAdapter(
    private val items: List<CategorySpendingSummary>,
    private val totalSpent: Double,
    private val currencyCode: String = "ZAR"
) : RecyclerView.Adapter<CategoryBreakdownAdapter.ViewHolder>() {

    private val colors = listOf(
        com.example.fintrackpro.R.color.primary_blue,
        com.example.fintrackpro.R.color.primary_green,
        com.example.fintrackpro.R.color.error_red,
        com.example.fintrackpro.R.color.purple_200,
        com.example.fintrackpro.R.color.teal_200,
        com.example.fintrackpro.R.color.orange
    )

    inner class ViewHolder(private val binding: ItemCategoryBreakdownBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CategorySpendingSummary, position: Int) {
            binding.tvCategoryName.text = item.name
            binding.tvCategoryAmount.text = CurrencyFormatter.format(item.total, currencyCode)
            
            // Re-adding percentage calculation if needed, though removed from XML in last step
            // Let's assume we want a reward icon for "frugal" categories (low spend)
            if (item.total < 100.0 && totalSpent > 500.0) {
                binding.tvRewardIcon.visibility = android.view.View.VISIBLE
                binding.tvRewardIcon.text = "🏅" // Frugal badge
            } else {
                binding.tvRewardIcon.visibility = android.view.View.GONE
            }

            val colorRes = colors[position % colors.size]
            binding.viewColor.setBackgroundColor(binding.root.context.getColor(colorRes))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBreakdownBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount() = items.size
}