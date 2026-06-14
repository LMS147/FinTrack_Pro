package com.example.fintrackpro.ui.more

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fintrackpro.databinding.FragmentMoreBinding
import com.example.fintrackpro.ui.gamification.AchievementsActivity
import com.example.fintrackpro.ui.reports.ReportsActivity
import com.example.fintrackpro.ui.settings.SettingsActivity
import com.example.fintrackpro.ui.shared.SharedWalletActivity

class MoreFragment : Fragment() {

    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnReports.setOnClickListener {
            startActivity(Intent(requireContext(), ReportsActivity::class.java))
        }

        binding.btnSharedWallet.setOnClickListener {
            startActivity(Intent(requireContext(), SharedWalletActivity::class.java))
        }

        binding.btnAchievements.setOnClickListener {
            startActivity(Intent(requireContext(), AchievementsActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
