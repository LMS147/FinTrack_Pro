package com.example.fintrackpro.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fintrackpro.FinTrackApp
import com.example.fintrackpro.R
import com.example.fintrackpro.databinding.FragmentDashboardBinding
import com.example.fintrackpro.ui.transactions.AddTransactionActivity
import com.example.fintrackpro.utils.FormatUtils
import com.example.fintrackpro.utils.SessionManager
import kotlinx.coroutines.launch

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

        setupToolbar()
        setupRecyclerView()
        observeData()
        setupClickListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.inflateMenu(R.menu.dashboard_menu)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_profile -> {
                    // Navigate to settings which has profile info
                    findNavController().navigate(R.id.moreFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        transactionAdapter = RecentTransactionAdapter()
        binding.rvRecentTransactions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
        }
    }

    private fun observeData() {
        val sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()
        if (userId != null) {
            val repository = (requireActivity().application as FinTrackApp).userRepository
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    val user = repository.getUserById(userId)
                    if (user != null) {
                        binding.toolbar.subtitle = "Hi, ${user.fullName.split(" ")[0]}!"
                    }
                }
            }
        }

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

        viewModel.convertedRecentTransactions.observe(viewLifecycleOwner) { transactions ->
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
