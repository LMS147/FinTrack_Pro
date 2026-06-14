package com.example.fintrackpro.ui.expense

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fintrackpro.FinTrackApp
import com.example.fintrackpro.data.entity.TransactionEntity
import com.example.fintrackpro.databinding.ActivityExpenseDetailBinding
import com.example.fintrackpro.utils.FormatUtils
import kotlinx.coroutines.*

class ExpenseDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseDetailBinding
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var expenseId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        expenseId = intent.getStringExtra("expenseId")
        if (expenseId == null) finish()

        val app = application as FinTrackApp

        scope.launch {
            val expense = app.transactionRepository.getTransactionById(expenseId!!)
            val user = if (expense != null) app.userRepository.getUserById(expense.userId) else null
            val currency = user?.defaultCurrency ?: "ZAR"
            
            expense?.let { displayExpense(it, currency) }
            val photo = app.transactionRepository.getPhotoForExpense(expenseId!!)
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

    private fun displayExpense(transaction: TransactionEntity, currency: String) {
        binding.tvDescription.text = transaction.description
        binding.tvAmount.text = FormatUtils.formatCurrency(transaction.amount, currency)
        binding.tvDate.text = FormatUtils.formatDate(transaction.date)
        binding.tvCategory.text = transaction.categoryId
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
