package com.example.fintrackpro.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fintrackpro.databinding.FragmentDashboardBinding
import com.example.fintrackpro.ui.transactions.AddTransactionActivity
import com.example.fintrackpro.utils.FormatUtils

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var transactionAdapter: RecentTransactionAdapter

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

        setupRecyclerView()
        observeData()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        transactionAdapter = RecentTransactionAdapter()
        binding.rvRecentTransactions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
        }
    }

    private fun observeData() {
        viewModel.totalBalance.observe(viewLifecycleOwner) { balance ->
            binding.tvTotalBalance.text = FormatUtils.formatCurrency(balance ?: 0.0)
        }

        viewModel.monthlyIncome.observe(viewLifecycleOwner) { income ->
            binding.tvMonthlyIncome.text = FormatUtils.formatCurrency(income ?: 0.0)
        }

        viewModel.monthlyExpenses.observe(viewLifecycleOwner) { expenses ->
            binding.tvMonthlyExpenses.text = FormatUtils.formatCurrency(expenses ?: 0.0)
        }

        viewModel.monthlySavings.observe(viewLifecycleOwner) { savings ->
            binding.tvMonthlySavings.text = FormatUtils.formatCurrency(savings)
        }

        viewModel.recentTransactions.observe(viewLifecycleOwner) { transactions ->
            if (transactions.isNullOrEmpty()) {
                binding.tvEmptyState.visibility = View.VISIBLE
                binding.rvRecentTransactions.visibility = View.GONE
            } else {
                binding.tvEmptyState.visibility = View.GONE
                binding.rvRecentTransactions.visibility = View.VISIBLE
                transactionAdapter.submitList(transactions)
            }
        }
    }

    private fun setupClickListeners() {
        binding.fabAddTransaction.setOnClickListener {
            startActivity(Intent(requireContext(), AddTransactionActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
