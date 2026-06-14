package com.example.fintrackpro.ui.budget

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.fintrackpro.data.Repository.BudgetRepository
import com.example.fintrackpro.databinding.ActivitySetBudgetBinding
import com.example.fintrackpro.utils.CurrencyFormatter
import com.example.fintrackpro.utils.SessionManager
import kotlinx.coroutines.*

class SetBudgetActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetBudgetBinding
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private val userId: Int by lazy { SessionManager(this).getUserId() }
    private var currentCurrency: String = "ZAR"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load user settings
        scope.launch {
            val db = com.example.fintrackpro.data.FinTrackDatabase.getDatabase(this@SetBudgetActivity)
            val user = db.userDao().getUserById(userId)
            currentCurrency = user?.defaultCurrency ?: "ZAR"
            
            // Initial text update
            binding.tvMinValue.text = CurrencyFormatter.format(binding.seekBarMin.progress.toDouble(), currentCurrency)
            binding.tvMaxValue.text = CurrencyFormatter.format(binding.seekBarMax.progress.toDouble(), currentCurrency)
        }

        // Setup SeekBars
        binding.seekBarMin.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val amount = progress.toDouble()
                binding.tvMinValue.text = CurrencyFormatter.format(amount, currentCurrency)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.seekBarMax.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val amount = progress.toDouble()
                binding.tvMaxValue.text = CurrencyFormatter.format(amount, currentCurrency)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.btnSaveBudget.setOnClickListener {
            val min = binding.seekBarMin.progress.toDouble()
            val max = binding.seekBarMax.progress.toDouble()
            if (max <= 0) {
                // show error
                return@setOnClickListener
            }
            saveBudget(min, max)
        }

        // Pre-load existing budget if available (optional)
        scope.launch {
            val db = com.example.fintrackpro.data.FinTrackDatabase.getDatabase(this@SetBudgetActivity)
            val budgetRepo = BudgetRepository(db.budgetDao())
            val monthYear = java.text.SimpleDateFormat("yyyy-MM", java.util.Locale.getDefault()).format(java.util.Date())
            val budget = budgetRepo.getBudgetForMonth(userId, monthYear)
            if (budget != null) {
                binding.seekBarMin.progress = (budget.minSpendingGoal ?: 0.0).toInt()
                binding.seekBarMax.progress = budget.maxSpendingGoal.toInt()
            }
        }
    }

    private fun saveBudget(min: Double, max: Double) {
        scope.launch {
            val db = com.example.fintrackpro.data.FinTrackDatabase.getDatabase(this@SetBudgetActivity)
            val budgetRepo = BudgetRepository(db.budgetDao())
            val monthYear = java.text.SimpleDateFormat("yyyy-MM", java.util.Locale.getDefault()).format(java.util.Date())
            budgetRepo.upsertBudget(userId, monthYear, min, max)
            finish()
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}