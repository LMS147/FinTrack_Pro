package com.example.fintrackpro.ui.expense

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.fintrackpro.data.entity.Transaction
import com.example.fintrackpro.data.entity.ExpensePhoto
import com.example.fintrackpro.data.Repository.CategoryRepository
import com.example.fintrackpro.data.Repository.ExpenseRepository
import com.example.fintrackpro.databinding.ActivityAddExpenseBinding
import com.example.fintrackpro.utils.PhotoHelper
import com.example.fintrackpro.utils.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpenseBinding
    private lateinit var expenseRepository: ExpenseRepository
    private lateinit var categoryRepository: CategoryRepository
    private var selectedPhotoUri: Uri? = null
    private var selectedCategoryId: Int? = null
    private var selectedDate: Date = Date()
    private var selectedStartTime: String? = null
    private var selectedEndTime: String? = null
    private val userId: Int by lazy { SessionManager(this).getUserId() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize repositories (replace with DI later)
        val database = com.example.fintrackpro.data.FinTrackDatabase.getDatabase(this)
        expenseRepository = ExpenseRepository(database.expenseDao())
        categoryRepository = CategoryRepository(database.categoryDao())

        setupCategoryDropdown()
        setupDatePickers()
        setupTimePickers()
        setupPhotoCapture()
        setupSaveButton()
    }

    private fun setupCategoryDropdown() {
        lifecycleScope.launchWhenCreated {
            val categories = categoryRepository.getCategoryList(userId)
            val adapter = ArrayAdapter(this@AddExpenseActivity, android.R.layout.simple_dropdown_item_1line, categories.map { it.name })
            binding.actvCategory.setAdapter(adapter)
            binding.actvCategory.setOnItemClickListener { parent, _, position, _ ->
                selectedCategoryId = categories[position].categoryId
            }
        }
    }

    private fun setupDatePickers() {
        binding.etDate.setOnClickListener {
            val cal = Calendar.getInstance()
            cal.time = selectedDate
            DatePickerDialog(this, { _, year, month, day ->
                cal.set(year, month, day)
                selectedDate = cal.time
                binding.etDate.setText(SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(selectedDate))
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun setupTimePickers() {
        binding.etStartTime.setOnClickListener {
            showTimePicker { time -> selectedStartTime = time; binding.etStartTime.setText(time) }
        }
        binding.etEndTime.setOnClickListener {
            showTimePicker { time -> selectedEndTime = time; binding.etEndTime.setText(time) }
        }
    }

    private fun showTimePicker(callback: (String) -> Unit) {
        val cal = Calendar.getInstance()
        TimePickerDialog(this, { _, hour, minute ->
            val formatted = String.format("%02d:%02d", hour, minute)
            callback(formatted)
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
    }

    private fun setupPhotoCapture() {
        binding.btnCamera.setOnClickListener {
            val uri = PhotoHelper.createImageFile(this)?.let { file ->
                FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
            }
            if (uri != null) {
                cameraLauncher.launch(uri)
            } else {
                Toast.makeText(this, "Cannot create file", Toast.LENGTH_SHORT).show()
            }
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
        binding.btnSave.setOnClickListener {
            // Reset errors
            binding.tilAmount.error = null
            binding.tilCategory.error = null
            binding.tilDescription.error = null

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

            val transaction = Transaction(
                userId = userId,
                categoryId = selectedCategoryId!!,
                amount = amount,
                description = description,
                date = selectedDate,
                startTime = selectedStartTime,
                endTime = selectedEndTime,
                isIncome = binding.chipIncome.isChecked
            )
            lifecycleScope.launch {
                val finalPhotoUri = selectedPhotoUri?.let { uri ->
                    // Copy to internal storage to ensure persistent access
                    if (uri.scheme == "content") {
                        val savedPath = PhotoHelper.saveImageToInternalStorage(this@AddExpenseActivity, uri)
                        if (savedPath != null) Uri.parse(savedPath) else null
                    } else {
                        uri
                    }
                }

                val expenseId = expenseRepository.addExpense(transaction).toInt()
                finalPhotoUri?.let { uri ->
                    expenseRepository.addPhoto(ExpensePhoto(expenseId = expenseId, photoUri = uri))
                }
                Toast.makeText(this@AddExpenseActivity, "Transaction saved", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}