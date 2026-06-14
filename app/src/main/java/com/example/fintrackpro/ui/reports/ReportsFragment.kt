package com.example.fintrackpro.ui.reports

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.fintrackpro.R
import com.example.fintrackpro.databinding.FragmentReportsBinding
import com.example.fintrackpro.utils.CurrencyFormatter
import com.example.fintrackpro.utils.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ReportsFragment : Fragment(R.layout.fragment_reports) {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    private val userId: Int by lazy { SessionManager(requireContext()).getUserId() }
    private val viewModel: ReportsViewModel by viewModels {
        ReportsViewModelFactory(requireContext(), userId)
    }

    private val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentReportsBinding.bind(view)

        setupDateButtons()
        observeUiState()
    }

    private fun setupDateButtons() {
        binding.btnStartDate.setOnClickListener {
            showDatePicker { date ->
                viewModel.setDateRange(date, viewModel.uiState.value.endDate ?: Calendar.getInstance().time)
                binding.btnStartDate.text = dateFormatter.format(date)
            }
        }

        binding.btnEndDate.setOnClickListener {
            showDatePicker { date ->
                viewModel.setDateRange(viewModel.uiState.value.startDate ?: Calendar.getInstance().time, date)
                binding.btnEndDate.text = dateFormatter.format(date)
            }
        }
    }

    private fun showDatePicker(callback: (Date) -> Unit) {
        val cal = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, year, month, day ->
            cal.set(year, month, day)
            callback(cal.time)
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.isLoading) return@collect

                    // Update pie chart
                    PieChartManager.setupPieChart(binding.pieChart, state.categoryTotals)

                    // Update Budget Analysis
                    updateBudgetAnalysis(state)

                    // Update breakdown list
                    if (state.categoryTotals.isEmpty()) {
                        binding.tvEmptyState.visibility = View.VISIBLE
                        binding.rvCategoryBreakdown.visibility = View.GONE
                    } else {
                        binding.tvEmptyState.visibility = View.GONE
                        binding.rvCategoryBreakdown.visibility = View.VISIBLE
                        binding.rvCategoryBreakdown.adapter = CategoryBreakdownAdapter(
                            state.categoryTotals,
                            state.totalSpent,
                            state.currency
                        )
                    }

                    // Update date button texts
                    state.startDate?.let { binding.btnStartDate.text = dateFormatter.format(it) }
                    state.endDate?.let { binding.btnEndDate.text = dateFormatter.format(it) }
                }
            }
        }
    }

    private fun updateBudgetAnalysis(state: ReportsViewModel.ReportsUiState) {
        val budget = state.budget
        if (budget == null) {
            binding.cardBudgetStatus.visibility = View.GONE
            return
        }

        binding.cardBudgetStatus.visibility = View.VISIBLE
        val totalSpent = state.totalSpent
        val maxGoal = budget.maxSpendingGoal
        val minGoal = budget.minSpendingGoal ?: 0.0
        val currency = state.currency

        binding.tvBudgetComparison.text = "Spent ${CurrencyFormatter.format(totalSpent, currency)} of ${CurrencyFormatter.format(maxGoal, currency)} max goal"

        val progress = if (maxGoal > 0) (totalSpent / maxGoal * 100).toInt() else 0
        binding.progressBudget.progress = progress.coerceIn(0, 100)

        when {
            totalSpent > maxGoal -> {
                binding.tvBudgetStatus.text = "Over Budget! 🚨 You've exceeded your maximum goal by ${CurrencyFormatter.format(totalSpent - maxGoal, currency)}."
                binding.tvBudgetStatus.setTextColor(requireContext().getColor(R.color.error_red))
                binding.progressBudget.setIndicatorColor(requireContext().getColor(R.color.error_red))
            }
            totalSpent >= minGoal -> {
                binding.tvBudgetStatus.text = "On Track! ✅ You're between your minimum and maximum goals."
                binding.tvBudgetStatus.setTextColor(requireContext().getColor(R.color.primary_green))
                binding.progressBudget.setIndicatorColor(requireContext().getColor(R.color.primary_green))
            }
            else -> {
                binding.tvBudgetStatus.text = "Under Minimum. 📉 You haven't reached your minimum spending goal yet."
                binding.tvBudgetStatus.setTextColor(requireContext().getColor(R.color.primary_blue))
                binding.progressBudget.setIndicatorColor(requireContext().getColor(R.color.primary_blue))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}