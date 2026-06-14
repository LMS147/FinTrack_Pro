package com.example.fintrackpro.ui.expense

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import com.example.fintrackpro.FinTrackApp
import com.example.fintrackpro.data.entity.TransactionEntity
import com.example.fintrackpro.data.entity.ExpensePhoto
import com.example.fintrackpro.databinding.ActivityAddExpenseBinding
import com.example.fintrackpro.utils.FileUtils
import com.example.fintrackpro.utils.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpenseBinding
    private var selectedPhotoUri: Uri? = null
    private var selectedCategoryId: String? = null
    private var selectedDate: Long = System.currentTimeMillis()
    private val userId: String by lazy { SessionManager(this).getUserId() ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCategoryDropdown()
        setupDatePickers()
        setupPhotoCapture()
        setupSaveButton()
    }

    private fun setupCategoryDropdown() {
        val app = application as FinTrackApp
        lifecycleScope.launch {
            val categories = app.categoryRepository.getAllCategories(userId).asFlow().first()
            val adapter = ArrayAdapter(this@AddExpenseActivity, android.R.layout.simple_dropdown_item_1line, categories.map { it.name })
            binding.actvCategory.setAdapter(adapter)
            binding.actvCategory.setOnItemClickListener { _, _, position, _ ->
                selectedCategoryId = categories[position].categoryId
            }
        }
    }

    private fun setupDatePickers() {
        binding.etDate.setOnClickListener {
            val cal = Calendar.getInstance()
            cal.timeInMillis = selectedDate
            DatePickerDialog(this, { _, year, month, day ->
                cal.set(year, month, day)
                selectedDate = cal.timeInMillis
                binding.etDate.setText(SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(cal.time))
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupPhotoCapture() {
        binding.btnCamera.setOnClickListener {
            val file = FileUtils.createImageFile(this)
            val uri = FileUtils.getUriForFile(this, file)
            selectedPhotoUri = uri
            cameraLauncher.launch(uri)
        }
        binding.btnGallery.setOnClickListener { galleryLauncher.launch("image/*") }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && selectedPhotoUri != null) {
            displayPreview(selectedPhotoUri)
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedPhotoUri = it
            displayPreview(it)
        }
    }

    private fun displayPreview(uri: Uri?) {
        uri?.let {
            binding.ivPreview.setImageURI(null)
            binding.ivPreview.setImageURI(it)
            binding.ivPreview.visibility = android.view.View.VISIBLE
        }
    }

    private fun setupSaveButton() {
        val app = application as FinTrackApp
        binding.btnSave.setOnClickListener {
            val amount = binding.etAmount.text.toString().toDoubleOrNull()
            if (amount == null || amount <= 0) {
                binding.tilAmount.error = "Enter a valid amount"
                return@setOnClickListener
            }
            if (selectedCategoryId == null) {
                binding.tilCategory.error = "Select a category"
                return@setOnClickListener
            }
            val description = binding.etDescription.text.toString().trim()
            if (description.isEmpty()) {
                binding.tilDescription.error = "Enter description"
                return@setOnClickListener
            }

            val transaction = TransactionEntity(
                userId = userId,
                accountId = "DEFAULT", // Placeholder
                categoryId = selectedCategoryId!!,
                amount = amount,
                description = description,
                type = if (binding.chipIncome.isChecked) "INCOME" else "EXPENSE",
                date = selectedDate
            )
            lifecycleScope.launch {
                val expenseId = app.transactionRepository.insertTransaction(transaction).toString()
                selectedPhotoUri?.let { uri ->
                    app.transactionRepository.insertPhoto(ExpensePhoto(expenseId = expenseId, photoUri = uri))
                }
                Toast.makeText(this@AddExpenseActivity, "Transaction saved", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
