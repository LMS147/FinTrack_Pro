package com.example.fintrackpro.ui.budget

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.fintrackpro.R
import com.example.fintrackpro.databinding.FragmentBudgetBinding
import com.example.fintrackpro.utils.CurrencyFormatter
import com.example.fintrackpro.utils.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class BudgetFragment : Fragment(R.layout.fragment_budget) {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private val userId: Int by lazy { SessionManager(requireContext()).getUserId() }
    private val viewModel: BudgetViewModel by viewModels {
        BudgetViewModelFactory(requireContext(), userId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBudgetBinding.bind(view)

        binding.btnSetBudget.setOnClickListener {
            startActivity(Intent(requireContext(), SetBudgetActivity::class.java))
        }

        observeUiState()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateUi(state)
                }
            }
        }
    }

    private fun updateUi(state: BudgetViewModel.BudgetUiState) {
        if (state.isLoading) return

        // Format month for display
        try {
            val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            val date = sdf.parse(state.monthYear)
            val displayFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            binding.tvMonth.text = displayFormat.format(date!!)
        } catch (e: Exception) {
            binding.tvMonth.text = state.monthYear
        }

        val budget = state.budget
        val totalSpent = state.totalSpent
        val currency = state.currency

        if (budget != null) {
            binding.tvMinGoal.text = CurrencyFormatter.format(budget.minSpendingGoal ?: 0.0, currency)
            binding.tvMaxGoal.text = CurrencyFormatter.format(budget.maxSpendingGoal, currency)

            // Spending progress
            binding.tvSpendingLabel.text = "Spent: ${CurrencyFormatter.format(totalSpent, currency)} of ${CurrencyFormatter.format(budget.maxSpendingGoal, currency)}"

            val percent = if (budget.maxSpendingGoal > 0) (totalSpent / budget.maxSpendingGoal * 100).toInt().coerceIn(0, 100) else 0
            binding.progressBar.progress = percent
            binding.tvPercentage.text = "$percent% of max goal"
        } else {
            binding.tvMinGoal.text = "—"
            binding.tvMaxGoal.text = "—"
            binding.tvSpendingLabel.text = "No budget set for this month"
            binding.progressBar.progress = 0
            binding.tvPercentage.text = ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}