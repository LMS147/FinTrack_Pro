package com.example.fintrackpro.ui.settings

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.fintrackpro.FinTrackApp
import com.example.fintrackpro.databinding.ActivitySettingsBinding
import com.example.fintrackpro.ui.auth.AuthActivity
import com.example.fintrackpro.utils.FormatUtils
import com.example.fintrackpro.utils.SessionManager
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setupToolbar()
        setupClickListeners()
        loadUserProfile()
        loadSettings()
    }

    private fun loadSettings() {
        binding.tvSelectedCurrency.text = sessionManager.getCurrency()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupClickListeners() {
        binding.tvProfileName.setOnClickListener {
            showEditNameDialog()
        }
        
        binding.btnCurrency.setOnClickListener {
            showCurrencyDialog()
        }
        
        binding.btnTheme.setOnClickListener {
            showThemeDialog()
        }

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun loadUserProfile() {
        val userId = sessionManager.getUserId() ?: return
        val repository = (application as FinTrackApp).userRepository
        
        lifecycleScope.launch {
            val user = repository.getUserById(userId)
            if (user != null) {
                binding.tvProfileName.text = user.fullName
                binding.tvProfileEmail.text = user.email
            }
        }
    }

    private fun showEditNameDialog() {
        val input = EditText(this)
        input.setText(binding.tvProfileName.text)
        
        AlertDialog.Builder(this)
            .setTitle("Edit Name")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val newName = input.text.toString()
                if (newName.isNotEmpty()) {
                    updateUserName(newName)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateUserName(newName: String) {
        val userId = sessionManager.getUserId() ?: return
        val repository = (application as FinTrackApp).userRepository
        
        lifecycleScope.launch {
            val user = repository.getUserById(userId)
            if (user != null) {
                val updatedUser = user.copy(fullName = newName)
                repository.updateUser(updatedUser)
                binding.tvProfileName.text = newName
                Toast.makeText(this@SettingsActivity, "Profile updated", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showCurrencyDialog() {
        val currencies = arrayOf("USD", "EUR", "GBP", "INR", "ZAR", "JPY", "CNY")
        AlertDialog.Builder(this)
            .setTitle("Select Currency")
            .setItems(currencies) { _, which ->
                val selected = currencies[which]
                binding.tvSelectedCurrency.text = selected
                sessionManager.saveCurrency(selected)
                FormatUtils.defaultCurrency = selected
                Toast.makeText(this, "Currency set to $selected", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun showThemeDialog() {
        val themes = arrayOf("System Default", "Light", "Dark")
        AlertDialog.Builder(this)
            .setTitle("Select Theme")
            .setItems(themes) { _, which ->
                val selected = themes[which]
                binding.tvSelectedTheme.text = selected
                // Here you would normally apply the theme
                Toast.makeText(this, "Theme set to $selected", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun logout() {
        sessionManager.clearSession()
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
