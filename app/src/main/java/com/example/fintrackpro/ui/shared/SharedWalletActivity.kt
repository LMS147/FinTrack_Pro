package com.example.fintrackpro.ui.shared

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.fintrackpro.databinding.ActivitySharedWalletBinding

class SharedWalletActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySharedWalletBinding
    private val viewModel: SharedWalletViewModel by viewModels()
    private lateinit var adapter: SharedWalletAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySharedWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Shared Wallets"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = SharedWalletAdapter { inviteCode ->
            copyToClipboard(inviteCode)
            Toast.makeText(this, "Invite code copied: $inviteCode", Toast.LENGTH_SHORT).show()
        }
        binding.rvSharedWallets.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.fabCreateWallet.setOnClickListener {
            showCreateWalletDialog()
        }
        binding.fabJoinWallet.setOnClickListener {
            showJoinWalletDialog()
        }
    }

    private fun observeViewModel() {
        viewModel.convertedWallets.observe(this) { wallets ->
            adapter.submitList(wallets)
            binding.tvEmptyState.visibility = if (wallets.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshCurrency()
    }

    private fun showCreateWalletDialog() {
        val input = EditText(this)
        input.hint = "Wallet Name"
        AlertDialog.Builder(this)
            .setTitle("Create Shared Wallet")
            .setView(input)
            .setPositiveButton("Create") { _, _ ->
                val name = input.text.toString()
                if (name.isNotEmpty()) {
                    viewModel.createSharedWallet(name, null)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showJoinWalletDialog() {
        val input = EditText(this)
        input.hint = "Invite Code"
        AlertDialog.Builder(this)
            .setTitle("Join Shared Wallet")
            .setView(input)
            .setPositiveButton("Join") { _, _ ->
                val code = input.text.toString()
                if (code.isNotEmpty()) {
                    viewModel.joinSharedWallet(code)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Invite Code", text)
        clipboard.setPrimaryClip(clip)
    }
}
