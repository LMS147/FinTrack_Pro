package com.example.fintrackpro.ui.accounts

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fintrackpro.databinding.FragmentAccountsBinding

class AccountsFragment : Fragment() {

    private var _binding: FragmentAccountsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AccountViewModel by viewModels()
    private lateinit var accountAdapter: AccountAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeData()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        accountAdapter = AccountAdapter()
        binding.rvAccounts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = accountAdapter
        }
    }

    private fun observeData() {
        viewModel.accounts.observe(viewLifecycleOwner) { accounts ->
            accountAdapter.submitList(accounts)
        }
    }

    private fun setupClickListeners() {
        binding.fabAddAccount.setOnClickListener {
            startActivity(Intent(requireContext(), AddAccountActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
