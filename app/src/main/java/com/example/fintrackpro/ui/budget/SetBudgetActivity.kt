package com.example.fintrackpro.ui.budget

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.fintrackpro.FinTrackApp
import com.example.fintrackpro.data.entity.BudgetEntity
import com.example.fintrackpro.databinding.ActivitySetBudgetBinding
import com.example.fintrackpro.utils.FormatUtils
import com.example.fintrackpro.utils.SessionManager
import kotlinx.coroutines.*

class SetBudgetActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetBudgetBinding
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private val userId: String by lazy { SessionManager(this).getUserId() ?: "" }
    private var currentCurrency: String = "ZAR"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val app = application as FinTrackApp

        // Load user settings
        scope.launch {
            val user = app.userRepository.getUserById(userId)
            currentCurrency = user?.defaultCurrency ?: "ZAR"
            
            // Initial text update
            binding.tvMinValue.text = FormatUtils.formatCurrency(binding.seekBarMin.progress.toDouble(), currentCurrency)
            binding.tvMaxValue.text = FormatUtils.formatCurrency(binding.seekBarMax.progress.toDouble(), currentCurrency)
        }

        // Setup SeekBars
        binding.seekBarMin.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val amount = progress.toDouble()
                binding.tvMinValue.text = FormatUtils.formatCurrency(amount, currentCurrency)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.seekBarMax.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val amount = progress.toDouble()
                binding.tvMaxValue.text = FormatUtils.formatCurrency(amount, currentCurrency)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.btnSaveBudget.setOnClickListener {
            val max = binding.seekBarMax.progress.toDouble()
            if (max <= 0) {
                return@setOnClickListener
            }
            saveBudget(max)
        }

        // Pre-load existing budget
        scope.launch {
            val budgets = app.budgetRepository.getBudgetsByUser(userId)
            // Simplified for refactor
        }
    }

    private fun saveBudget(amount: Double) {
        scope.launch {
            val app = application as FinTrackApp
            val (start, end) = FormatUtils.getMonthStartEnd()
            val newBudget = BudgetEntity(
                userId = userId,
                categoryId = "DEFAULT", // Placeholder
                categoryName = "Overall",
                amount = amount,
                period = "MONTHLY",
                startDate = start,
                endDate = end
            )
            app.budgetRepository.insertBudget(newBudget)
            finish()
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
