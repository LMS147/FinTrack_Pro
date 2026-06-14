package com.example.fintrackpro.ui.budget

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.fintrackpro.R
import com.example.fintrackpro.databinding.FragmentBudgetBinding
import com.example.fintrackpro.utils.FormatUtils

class BudgetFragment : Fragment(R.layout.fragment_budget) {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BudgetViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBudgetBinding.bind(view)

        binding.btnSetBudget.setOnClickListener {
            startActivity(Intent(requireContext(), SetBudgetActivity::class.java))
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.budgets.observe(viewLifecycleOwner) { budgets ->
            val budget = budgets.firstOrNull()
            if (budget != null) {
                binding.tvMinGoal.text = "—"
                binding.tvMaxGoal.text = FormatUtils.formatCurrency(budget.amount)
                binding.tvSpendingLabel.text = "Budget: ${FormatUtils.formatCurrency(budget.amount)}"
                
                // Note: The new BudgetViewModel doesn't currently expose totalSpent for the progress bar.
                // You might need to add a MediatorLiveData or similar to provide the progress.
                binding.progressBar.progress = 0 
                binding.tvPercentage.text = ""
            } else {
                binding.tvMinGoal.text = "—"
                binding.tvMaxGoal.text = "—"
                binding.tvSpendingLabel.text = "No budget set"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
