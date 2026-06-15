package com.example.fintrackpro.ui.gamification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fintrackpro.R
import com.example.fintrackpro.data.entity.AchievementEntity
import com.example.fintrackpro.databinding.ItemAchievementBinding

class AchievementAdapter : ListAdapter<AchievementEntity, AchievementAdapter.AchievementViewHolder>(AchievementDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val binding = ItemAchievementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AchievementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AchievementViewHolder(private val binding: ItemAchievementBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(achievement: AchievementEntity) {
            binding.tvTitle.text = achievement.title
            binding.tvDescription.text = achievement.description
            binding.tvPoints.text = "${achievement.points} pts"
            binding.tvProgress.text = "${achievement.progress}/${achievement.target}"
            binding.progressIndicator.max = achievement.target
            binding.progressIndicator.progress = achievement.progress

            if (achievement.isUnlocked) {
                binding.tvStatus.text = "Unlocked"
                binding.tvStatus.setTextColor(ContextCompat.getColor(itemView.context, R.color.secondary))
                binding.ivBadge.alpha = 1.0f
            } else {
                binding.tvStatus.text = "Locked"
                binding.tvStatus.setTextColor(ContextCompat.getColor(itemView.context, android.R.color.darker_gray))
                binding.ivBadge.alpha = 0.5f
            }
        }
    }

    class AchievementDiffCallback : DiffUtil.ItemCallback<AchievementEntity>() {
        override fun areItemsTheSame(oldItem: AchievementEntity, newItem: AchievementEntity): Boolean {
            return oldItem.achievementId == newItem.achievementId
        }

        override fun areContentsTheSame(oldItem: AchievementEntity, newItem: AchievementEntity): Boolean {
            return oldItem == newItem
        }
    }
}
