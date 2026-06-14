package com.example.fintrackpro.ui.transactions

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fintrackpro.databinding.FragmentTransactionsBinding
import com.example.fintrackpro.ui.transactions.TransactionViewModel

class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransactionViewModel by viewModels()
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeData()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter()
        binding.rvTransactions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
        }
    }

    private fun observeData() {
        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.submitList(transactions)
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
