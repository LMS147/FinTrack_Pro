package com.example.fintrackpro.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.fintrackpro.R
import com.example.fintrackpro.databinding.ActivityLoginBinding
import com.example.fintrackpro.ui.MainActivity
import com.example.fintrackpro.utils.AuthViewModelFactory
import com.example.fintrackpro.utils.hideKeyboard
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels {
        // Inject repository via DI (simplified for example)
        AuthViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeLoginState()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString()
            hideKeyboard()
            viewModel.login(username, password)
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.tvForgotPassword.setOnClickListener {
            // TODO: Implement password reset flow
            Toast.makeText(this, "Password reset coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeLoginState() {
        lifecycleScope.launch {
            viewModel.loginState.observe(this@LoginActivity) { state ->
                when (state) {
                    is LoginState.Loading -> {
                        binding.progressBar.visibility = android.view.View.VISIBLE
                        binding.btnLogin.isEnabled = false
                        binding.tvError.visibility = android.view.View.GONE
                    }
                    is LoginState.Success -> {
                        binding.progressBar.visibility = android.view.View.GONE
                        // Navigate to MainActivity
                        val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        finish()
                    }
                    is LoginState.Error -> {
                        binding.progressBar.visibility = android.view.View.GONE
                        binding.btnLogin.isEnabled = true
                        binding.tvError.text = state.message
                        binding.tvError.visibility = android.view.View.VISIBLE
                    }
                    else -> {
                        binding.progressBar.visibility = android.view.View.GONE
                        binding.btnLogin.isEnabled = true
                    }
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