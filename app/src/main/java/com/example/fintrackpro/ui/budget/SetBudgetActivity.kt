package com.example.fintrackpro.ui.budget

import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.fintrackpro.databinding.ActivitySetBudgetBinding
import com.example.fintrackpro.utils.FormatUtils

class SetBudgetActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetBudgetBinding
    private val viewModel: BudgetViewModel by viewModels()
    private var selectedCategoryId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCategoryDropdown()
        setupSeekBar()
        setupSaveButton()
        observeSaveState()
    }

    private fun setupCategoryDropdown() {
        viewModel.expenseCategories.observe(this) { categories ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories.map { it.name })
            binding.actvCategory.setAdapter(adapter)
            binding.actvCategory.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                selectedCategoryId = categories[position].categoryId
            }
        }
    }

    private fun setupSeekBar() {
        binding.seekBarMax.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvMaxValue.text = FormatUtils.formatCurrency(progress.toDouble())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupSaveButton() {
        binding.btnSaveBudget.setOnClickListener {
            val amount = binding.seekBarMax.progress.toDouble()
            if (amount <= 0) {
                Toast.makeText(this, "Please set an amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedCategoryId == null) {
                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val (start, end) = FormatUtils.getMonthStartEnd()
            viewModel.addBudget(
                categoryId = selectedCategoryId!!,
                categoryName = binding.actvCategory.text.toString(),
                amount = amount,
                period = "MONTHLY",
                startDate = start,
                endDate = end,
                alertThreshold = 80
            )
        }
    }

    private fun observeSaveState() {
        viewModel.saveState.observe(this) { state ->
            when (state) {
                is SaveState.Loading -> binding.btnSaveBudget.isEnabled = false
                is SaveState.Success -> {
                    Toast.makeText(this, "Budget saved", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is SaveState.Error -> {
                    binding.btnSaveBudget.isEnabled = true
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
