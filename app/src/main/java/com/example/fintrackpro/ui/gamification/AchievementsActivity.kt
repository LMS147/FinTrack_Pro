package com.example.fintrackpro.ui.gamification

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.fintrackpro.databinding.ActivityAchievementsBinding

class AchievementsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAchievementsBinding
    private val viewModel: AchievementViewModel by viewModels()
    private lateinit var adapter: AchievementAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAchievementsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Achievements"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = AchievementAdapter()
        binding.rvAchievements.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.achievements.observe(this) { achievements ->
            adapter.submitList(achievements)
        }

        viewModel.totalPoints.observe(this) { points ->
            binding.tvTotalPoints.text = (points ?: 0).toString()
        }
    }
}
