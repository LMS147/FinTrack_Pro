package com.example.fintrackpro.ui.shared

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fintrackpro.databinding.ActivitySharedWalletBinding

class SharedWalletActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySharedWalletBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySharedWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Shared Wallet"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }
}
