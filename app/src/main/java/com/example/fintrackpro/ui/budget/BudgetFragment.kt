package com.example.fintrackpro.ui.budget

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.fintrackpro.FinTrackApp
import com.example.fintrackpro.R
import com.example.fintrackpro.databinding.FragmentBudgetBinding
import com.example.fintrackpro.utils.FormatUtils
import com.example.fintrackpro.utils.SessionManager
import kotlinx.coroutines.launch

class BudgetFragment : Fragment(R.layout.fragment_budget) {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private val userId: String by lazy { SessionManager(requireContext()).getUserId() ?: "" }
    private val viewModel: BudgetViewModel by viewModels {
        val app = requireActivity().application as FinTrackApp
        BudgetViewModelFactory(app.budgetRepository, app.transactionRepository, app.userRepository, userId)
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

        binding.tvMonth.text = "Monthly Budget" // Simple display for now

        val budget = state.budget
        val totalSpent = state.totalSpent
        val currency = state.currency

        if (budget != null) {
            // Adjusting based on new BudgetEntity fields: amount
            binding.tvMinGoal.text = "—" // Min goal removed in new schema
            binding.tvMaxGoal.text = FormatUtils.formatCurrency(budget.amount, currency)

            binding.tvSpendingLabel.text = "Spent: ${FormatUtils.formatCurrency(totalSpent, currency)} of ${FormatUtils.formatCurrency(budget.amount, currency)}"

            val percent = if (budget.amount > 0) (totalSpent / budget.amount * 100).toInt().coerceIn(0, 100) else 0
            binding.progressBar.progress = percent
            binding.tvPercentage.text = "$percent% of budget"
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
