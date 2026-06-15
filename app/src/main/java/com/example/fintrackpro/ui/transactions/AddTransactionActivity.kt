package com.example.fintrackpro.ui.transactions

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fintrackpro.R
import com.example.fintrackpro.databinding.ActivityAddTransactionBinding
import com.example.fintrackpro.ui.transactions.TransactionViewModel
import com.example.fintrackpro.ui.transactions.SaveState
import com.example.fintrackpro.utils.FileUtils
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding
    private val viewModel: TransactionViewModel by viewModels()
    private var selectedDate: Long = System.currentTimeMillis()
    private var receiptImagePath: String? = null
    private var currentPhotoPath: String? = null

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            receiptImagePath = currentPhotoPath
            binding.tvReceiptStatus.text = "Receipt attached"
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            receiptImagePath = it.toString()
            binding.tvReceiptStatus.text = "Receipt attached"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupSpinners()
        setupClickListeners()
        observeData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add Transaction"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupSpinners() {
        val typeAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listOf("EXPENSE", "INCOME")
        )
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerType.adapter = typeAdapter

        binding.spinnerType.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                updateCategorySpinner()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        viewModel.accounts.observe(this) { accounts ->
            val accountNames = accounts.map { it.accountName }
            val accountAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                accountNames
            )
            accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerAccount.adapter = accountAdapter
        }

        viewModel.expenseCategories.observe(this) { 
            if (binding.spinnerType.selectedItem?.toString() == "EXPENSE") {
                updateCategorySpinner()
            }
        }

        viewModel.incomeCategories.observe(this) {
            if (binding.spinnerType.selectedItem?.toString() == "INCOME") {
                updateCategorySpinner()
            }
        }
    }

    private fun updateCategorySpinner() {
        val type = binding.spinnerType.selectedItem?.toString() ?: "EXPENSE"
        val categories = if (type == "EXPENSE") {
            viewModel.expenseCategories.value ?: emptyList()
        } else {
            viewModel.incomeCategories.value ?: emptyList()
        }

        val categoryNames = categories.map { it.name }
        val categoryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categoryNames
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = categoryAdapter
    }

    private fun setupClickListeners() {
        binding.btnSelectDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnAttachReceipt.setOnClickListener {
            showImagePickerDialog()
        }

        binding.btnSave.setOnClickListener {
            saveTransaction()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDate

        DatePickerDialog(
            this,
            { _, year, month, day ->
                calendar.set(year, month, day)
                selectedDate = calendar.timeInMillis
                binding.tvSelectedDate.text = android.text.format.DateFormat.format("MMM dd, yyyy", selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Select Image Source")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
            return
        }

        val photoFile = FileUtils.createImageFile(this)
        currentPhotoPath = photoFile.absolutePath
        val photoUri = FileUtils.getUriForFile(this, photoFile)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        cameraLauncher.launch(intent)
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun saveTransaction() {
        val title = binding.etTitle.text.toString().trim()
        val amountStr = binding.etAmount.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()

        if (title.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        val type = binding.spinnerType.selectedItem.toString()
        val accountPosition = binding.spinnerAccount.selectedItemPosition
        val categoryPosition = binding.spinnerCategory.selectedItemPosition

        val account = viewModel.accounts.value?.getOrNull(accountPosition)
        if (account == null) {
            Toast.makeText(this, "Please create an account first", Toast.LENGTH_SHORT).show()
            return
        }

        val categories = if (type == "EXPENSE") {
            viewModel.expenseCategories.value
        } else {
            viewModel.incomeCategories.value
        }

        val category = categories?.getOrNull(categoryPosition)
        if (category == null) {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.addTransaction(
            accountId = account.accountId,
            categoryId = category.categoryId,
            type = type,
            amount = amount,
            title = title,
            description = description.ifEmpty { null },
            date = selectedDate,
            receiptImagePath = receiptImagePath
        )
    }

    private fun observeData() {
        viewModel.saveState.observe(this) { state ->
            when (state) {
                is SaveState.Loading -> {
                    binding.btnSave.isEnabled = false
                }
                is SaveState.Success -> {
                    Toast.makeText(this, "Transaction added successfully", Toast.LENGTH_SHORT).show()
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
