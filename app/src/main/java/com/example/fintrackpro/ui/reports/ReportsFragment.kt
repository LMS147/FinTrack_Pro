package com.example.fintrackpro.ui.reports

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.fintrackpro.R
import com.example.fintrackpro.databinding.FragmentReportsBinding
import com.example.fintrackpro.utils.FormatUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ReportsFragment : Fragment(R.layout.fragment_reports) {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReportsViewModel by viewModels()

    private val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentReportsBinding.bind(view)

        observeUiState()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.isLoading) return@collect

                    updateBudgetAnalysis(state)

                    if (state.categoryTotals.isEmpty()) {
                        binding.tvEmptyState.visibility = View.VISIBLE
                        binding.rvCategoryBreakdown.visibility = View.GONE
                    } else {
                        binding.tvEmptyState.visibility = View.GONE
                        binding.rvCategoryBreakdown.visibility = View.VISIBLE
                    }

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
        val maxGoal = budget.amount
        val currency = state.currency

        binding.tvBudgetComparison.text = "Spent ${FormatUtils.formatCurrency(totalSpent, currency)} of ${FormatUtils.formatCurrency(maxGoal, currency)} budget"

        val progress = if (maxGoal > 0) (totalSpent / maxGoal * 100).toInt() else 0
        binding.progressBudget.progress = progress.coerceIn(0, 100)

        when {
            totalSpent > maxGoal -> {
                binding.tvBudgetStatus.text = "Over Budget! 🚨 You've exceeded your goal."
                binding.tvBudgetStatus.setTextColor(requireContext().getColor(R.color.error_red))
            }
            else -> {
                binding.tvBudgetStatus.text = "On Track! ✅"
                binding.tvBudgetStatus.setTextColor(requireContext().getColor(R.color.primary_green))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
