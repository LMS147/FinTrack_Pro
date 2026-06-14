package com.example.fintrackpro.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.fintrackpro.R
import com.example.fintrackpro.databinding.ActivityMainBinding
import com.example.fintrackpro.ui.auth.LoginActivity
import com.example.fintrackpro.utils.SessionManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.util.Date

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sessionManager = SessionManager(this)
        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        
        checkConsistencyReward()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get NavHostFragment and NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Connect bottom navigation to the NavController
        val bottomNav: BottomNavigationView = binding.bottomNavigation
        bottomNav.setupWithNavController(navController)
    }

    private fun checkConsistencyReward() {
        val sessionManager = SessionManager(this)
        val userId = sessionManager.getUserId()
        if (userId == -1) return

        val database = com.example.fintrackpro.data.FinTrackDatabase.getDatabase(this)
        val expenseDao = database.expenseDao()

        lifecycleScope.launch {
            val oneDayAgo = Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)
            val recentCount = expenseDao.getExpensesCountBetweenDates(userId, oneDayAgo, Date())
            
            if (recentCount > 0) {
                android.widget.Toast.makeText(this@MainActivity, "🔥 Consistency Streak! Keep it up!", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }
}
