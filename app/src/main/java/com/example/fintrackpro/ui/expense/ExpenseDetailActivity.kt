package com.example.fintrackpro.ui.expense

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fintrackpro.data.entity.Transaction
import com.example.fintrackpro.data.Repository.ExpenseRepository
import com.example.fintrackpro.databinding.ActivityExpenseDetailBinding
import com.example.fintrackpro.utils.CurrencyFormatter
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.Locale

class ExpenseDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseDetailBinding
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var expenseId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        expenseId = intent.getIntExtra("expenseId", -1)
        if (expenseId == -1) finish()

        val db = com.example.fintrackpro.data.FinTrackDatabase.getDatabase(this)
        val repo = ExpenseRepository(db.expenseDao())

        scope.launch {
            val expense = repo.getExpenseById(expenseId)
            val user = db.userDao().getUserById(expense?.userId ?: 1)
            val currency = user?.defaultCurrency ?: "ZAR"
            
            expense?.let { displayExpense(it, currency) }
            val photo = repo.getPhotoForExpense(expenseId)
            if (photo != null) {
                try {
                    binding.ivPhoto.setImageURI(photo.photoUri)
                    binding.ivPhoto.visibility = android.view.View.VISIBLE
                } catch (e: SecurityException) {
                    binding.ivPhoto.visibility = android.view.View.GONE
                }
            }
        }
    }

    private fun displayExpense(transaction: Transaction, currency: String) {
        binding.tvDescription.text = transaction.description
        binding.tvAmount.text = CurrencyFormatter.format(transaction.amount, currency)
        binding.tvDate.text = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(transaction.date)
        binding.tvStartTime.text = transaction.startTime ?: "N/A"
        binding.tvEndTime.text = transaction.endTime ?: "N/A"
        binding.tvCategory.text = "Category " + transaction.categoryId // could fetch name
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}