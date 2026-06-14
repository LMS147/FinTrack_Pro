package com.example.fintrackpro.ui.budgets

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.fintrackpro.databinding.ActivityAddBudgetBinding
import com.example.fintrackpro.ui.budget.BudgetViewModel
import com.example.fintrackpro.ui.budget.SaveState
import com.example.fintrackpro.utils.FormatUtils
import java.util.*

class AddBudgetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBudgetBinding
    private val viewModel: BudgetViewModel by viewModels()
    private var startDate: Long = System.currentTimeMillis()
    private var endDate: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupSpinners()
        setupClickListeners()
        observeData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add Budget"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupSpinners() {
        val periodAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listOf("DAILY", "WEEKLY", "MONTHLY")
        )
        periodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPeriod.adapter = periodAdapter

        viewModel.expenseCategories.observe(this) { categories ->
            val categoryNames = categories.map { it.name }
            val categoryAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                categoryNames
            )
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerCategory.adapter = categoryAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnSelectStartDate.setOnClickListener {
            showDatePicker(true)
        }

        binding.btnSelectEndDate.setOnClickListener {
            showDatePicker(false)
        }

        binding.btnSave.setOnClickListener {
            saveBudget()
        }
    }

    private fun showDatePicker(isStartDate: Boolean) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = if (isStartDate) startDate else endDate

        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day)
                if (isStartDate) {
                    startDate = calendar.timeInMillis
                    binding.tvStartDate.text = FormatUtils.formatDate(startDate)
                } else {
                    endDate = calendar.timeInMillis
                    binding.tvEndDate.text = FormatUtils.formatDate(endDate)
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun saveBudget() {
        val amountStr = binding.etAmount.text.toString().trim()
        val alertThresholdStr = binding.etAlertThreshold.text.toString().trim()

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter budget amount", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        val alertThreshold = alertThresholdStr.toIntOrNull() ?: 80
        val period = binding.spinnerPeriod.selectedItem.toString()
        val categoryPosition = binding.spinnerCategory.selectedItemPosition

        viewModel.expenseCategories.value?.getOrNull(categoryPosition)?.let { category ->
            viewModel.addBudget(
                categoryId = category.categoryId,
                categoryName = category.name,
                amount = amount,
                period = period,
                startDate = startDate,
                endDate = endDate,
                alertThreshold = alertThreshold
            )
        }
    }

    private fun observeData() {
        viewModel.saveState.observe(this) { state ->
            when (state) {
                is SaveState.Loading -> {
                    binding.btnSave.isEnabled = false
                }
                is SaveState.Success -> {
                    Toast.makeText(this, "Budget added successfully", Toast.LENGTH_SHORT).show()
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
