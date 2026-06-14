package com.example.fintrackpro.ui.expense

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fintrackpro.FinTrackApp
import com.example.fintrackpro.R
import com.example.fintrackpro.databinding.FragmentExpenseBinding
import com.example.fintrackpro.utils.SessionManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ExpenseFragment : Fragment(R.layout.fragment_expense) {

    private var _binding: FragmentExpenseBinding? = null
    private val binding get() = _binding!!
    private val userId: String by lazy { SessionManager(requireContext()).getUserId() ?: "" }
    private lateinit var expenseAdapter: ExpenseAdapter

    private val viewModel: ExpenseViewModel by viewModels {
        val app = requireActivity().application as FinTrackApp
        ExpenseViewModelFactory(app.transactionRepository, app.userRepository, userId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentExpenseBinding.bind(view)

        setupRecyclerView()
        observeUiState()

        binding.btnFilter.setOnClickListener {
            // TODO: showDateRangePicker()
        }

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(requireContext(), AddExpenseActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter { expense ->
            val intent = Intent(requireContext(), ExpenseDetailActivity::class.java).apply {
                putExtra("expenseId", expense.transactionId)
            }
            startActivity(intent)
        }
        
        binding.rvExpenses.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = expenseAdapter
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder): Boolean = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val expense = expenseAdapter.getExpenseAt(viewHolder.adapterPosition)
                viewModel.deleteExpense(expense)
                
                Snackbar.make(binding.root, "Transaction deleted", Snackbar.LENGTH_LONG).show()
            }
        }).attachToRecyclerView(binding.rvExpenses)
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    if (state.isLoading) {
                        binding.shimmerView.startShimmer()
                        binding.shimmerView.visibility = View.VISIBLE
                        binding.rvExpenses.visibility = View.GONE
                        return@collect
                    }
                    
                    binding.shimmerView.stopShimmer()
                    binding.shimmerView.visibility = View.GONE
                    binding.rvExpenses.visibility = View.VISIBLE

                    if (state.error != null) {
                        return@collect
                    }
                    expenseAdapter.submitList(state.expenses)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
