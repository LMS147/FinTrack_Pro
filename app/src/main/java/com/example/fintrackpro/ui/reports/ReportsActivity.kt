package com.example.fintrackpro.ui.reports

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fintrackpro.databinding.ActivityReportsBinding
import com.example.fintrackpro.utils.FormatUtils
import kotlinx.coroutines.launch

class ReportsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportsBinding
    private val viewModel: ReportsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        observeUiState()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Reports & Analytics"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        binding.rvCategoryBreakdown.layoutManager = LinearLayoutManager(this)
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.isLoading) return@collect

                    binding.tvTotalIncome.text = FormatUtils.formatCurrency(state.totalIncome, state.currency)
                    binding.tvTotalExpense.text = FormatUtils.formatCurrency(state.totalSpent, state.currency)

                    // Pie Chart
                    PieChartManager.setupPieChart(binding.pieChart, state.categoryTotals)

                    // Breakdown List
                    if (state.categoryTotals.isEmpty()) {
                        binding.rvCategoryBreakdown.visibility = View.GONE
                    } else {
                        binding.rvCategoryBreakdown.visibility = View.VISIBLE
                        binding.rvCategoryBreakdown.adapter = CategoryBreakdownAdapter(state.categoryTotals, state.currency)
                    }
                }
            }
        }
    }
}
