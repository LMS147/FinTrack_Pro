package com.example.fintrackpro.ui.budgets

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fintrackpro.databinding.ActivityBudgetDetailsBinding

class BudgetDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBudgetDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Budget Details"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }
}
