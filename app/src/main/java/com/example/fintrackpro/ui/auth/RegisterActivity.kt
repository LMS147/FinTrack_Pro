package com.example.fintrackpro.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.fintrackpro.FinTrackApp
import com.example.fintrackpro.databinding.ActivityRegisterBinding
import com.example.fintrackpro.utils.AuthViewModelFactory
import com.example.fintrackpro.utils.SessionManager

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels {
        val app = application as FinTrackApp
        AuthViewModelFactory(app.userRepository, SessionManager(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeRegisterState()
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            val fullName = binding.etDisplayName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            hideKeyboard()
            viewModel.register(
                email = email,
                password = password,
                confirmPassword = confirmPassword,
                fullName = fullName
            )
        }

        binding.ibBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.tvLogin.setOnClickListener {
            finish() // Return to login
        }
    }

    private fun observeRegisterState() {
        viewModel.registerState.observe(this) { state ->
            when (state) {
                is RegisterState.Loading -> {
                    binding.progressBar.visibility = android.view.View.VISIBLE
                    binding.btnRegister.isEnabled = false
                    binding.tvError.visibility = android.view.View.GONE
                }
                is RegisterState.Success -> {
                    binding.progressBar.visibility = android.view.View.GONE
                    Toast.makeText(
                        this,
                        "Registration successful! Please login.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
                is RegisterState.Error -> {
                    binding.progressBar.visibility = android.view.View.GONE
                    binding.btnRegister.isEnabled = true
                    binding.tvError.text = state.message
                    binding.tvError.visibility = android.view.View.VISIBLE
                }
                else -> {
                    binding.progressBar.visibility = android.view.View.GONE
                    binding.btnRegister.isEnabled = true
                }
            }
        }
    }

    private fun hideKeyboard() {
        currentFocus?.let { view ->
            val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
