package com.example.fintrackpro.ui.expense

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.fintrackpro.databinding.ActivityAddExpenseBinding
import com.example.fintrackpro.utils.FileUtils
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpenseBinding
    private var selectedPhotoUri: Uri? = null
    private var selectedCategoryId: String? = null
    private var selectedDate: Long = System.currentTimeMillis()

    private val viewModel: TransactionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCategoryDropdown()
        setupDatePickers()
        setupPhotoCapture()
        setupSaveButton()
        observeSaveState()
    }

    private fun setupCategoryDropdown() {
        viewModel.expenseCategories.observe(this) { categories ->
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories.map { it.name })
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
            val title = binding.etDescription.text.toString().trim() // Using as title
            if (title.isEmpty()) {
                binding.tilDescription.error = "Enter title"
                return@setOnClickListener
            }

            viewModel.addTransaction(
                accountId = "DEFAULT",
                categoryId = selectedCategoryId!!,
                type = if (binding.chipIncome.isChecked) "INCOME" else "EXPENSE",
                amount = amount,
                title = title,
                description = "",
                date = selectedDate,
                receiptImagePath = selectedPhotoUri?.toString()
            )
        }
    }

    private fun observeSaveState() {
        viewModel.saveState.observe(this) { state ->
            when (state) {
                is SaveState.Loading -> binding.btnSave.isEnabled = false
                is SaveState.Success -> {
                    Toast.makeText(this, "Transaction saved", Toast.LENGTH_SHORT).show()
                    finish()
                }
                is SaveState.Error -> {
                    binding.btnSave.isEnabled = true
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
