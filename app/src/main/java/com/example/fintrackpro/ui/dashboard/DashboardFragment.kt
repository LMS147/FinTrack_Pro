package com.example.fintrackpro.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fintrackpro.FinTrackApp
import com.example.fintrackpro.databinding.FragmentDashboardBinding
import com.example.fintrackpro.ui.expense.AddExpenseActivity
import com.example.fintrackpro.utils.FormatUtils
import com.example.fintrackpro.utils.SessionManager
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val userId: String by lazy { SessionManager(requireContext()).getUserId() ?: "" }

    private val viewModel: DashboardViewModel by viewModels {
        val app = requireActivity().application as FinTrackApp
        DashboardViewModelFactory(app.transactionRepository, app.userRepository, userId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = bars.top)
            insets
        }

        setupRecyclerView()
        setupFab()
        observeUiState()
    }

    private fun setupRecyclerView() {
        binding.rvRecentTransactions.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupFab() {
        binding.fabAddExpense.setOnClickListener {
            startActivity(Intent(requireContext(), AddExpenseActivity::class.java))
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateUI(state)
                }
            }
        }
    }

    private fun updateUI(state: DashboardUiState) {
        if (state.isLoading) {
            binding.shimmerView.startShimmer()
            binding.shimmerView.visibility = View.VISIBLE
            binding.contentView.visibility = View.GONE
            return
        }

        binding.shimmerView.stopShimmer()
        binding.shimmerView.visibility = View.GONE
        binding.contentView.visibility = View.VISIBLE

        state.errorMessage?.let {
            return
        }

        val currency = state.currency

        binding.tvBalance.text = FormatUtils.formatCurrency(state.totalBalance, currency)
        binding.tvIncome.text = FormatUtils.formatCurrency(state.totalIncome, currency)
        binding.tvExpenses.text = FormatUtils.formatCurrency(state.totalExpenses, currency)

        if (state.recentTransactions.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.rvRecentTransactions.adapter = null
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.rvRecentTransactions.adapter = RecentTransactionsAdapter(state.recentTransactions, currency)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
