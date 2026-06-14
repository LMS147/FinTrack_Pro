package com.example.fintrackpro.ui.budgets

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fintrackpro.databinding.FragmentBudgetsBinding

class BudgetsFragment : Fragment() {

    private var _binding: FragmentBudgetsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BudgetViewModel by viewModels()
    private lateinit var budgetAdapter: BudgetAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeData()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        budgetAdapter = BudgetAdapter()
        binding.rvBudgets.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = budgetAdapter
        }
    }

    private fun observeData() {
        viewModel.budgets.observe(viewLifecycleOwner) { budgets ->
            budgetAdapter.submitList(budgets)
        }
    }

    private fun setupClickListeners() {
        binding.fabAddBudget.setOnClickListener {
            startActivity(Intent(requireContext(), AddBudgetActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
