package com.example.fintrackpro.ui.accounts

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.fintrackpro.databinding.ActivityAddAccountBinding
import com.example.fintrackpro.ui.account.AccountViewModel
import com.example.fintrackpro.ui.account.SaveState

class AddAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddAccountBinding
    private val viewModel: AccountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupSpinners()
        setupClickListeners()
        observeData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add Account"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupSpinners() {
        val typeAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listOf("BANK", "CASH", "CREDIT_CARD", "INVESTMENT")
        )
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerAccountType.adapter = typeAdapter

        val currencyAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listOf("USD", "EUR", "GBP", "INR", "JPY", "CNY", "AUD", "CAD")
        )
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCurrency.adapter = currencyAdapter
    }

    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {
            saveAccount()
        }
    }

    private fun saveAccount() {
        val name = binding.etAccountName.text.toString().trim()
        val balanceStr = binding.etInitialBalance.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter account name", Toast.LENGTH_SHORT).show()
            return
        }

        val balance = balanceStr.toDoubleOrNull() ?: 0.0
        val type = binding.spinnerAccountType.selectedItem.toString()
        val currency = binding.spinnerCurrency.selectedItem.toString()

        viewModel.addAccount(
            accountName = name,
            accountType = type,
            balance = balance,
            currency = currency,
            color = "#2196F3",
            icon = "account_balance"
        )
    }

    private fun observeData() {
        viewModel.saveState.observe(this) { state ->
            when (state) {
                is SaveState.Loading -> {
                    binding.btnSave.isEnabled = false
                }
                is SaveState.Success -> {
                    Toast.makeText(this, "Account added successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is SaveState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    binding.btnSave.isEnabled = true
                }
            }
        }
    }
}
