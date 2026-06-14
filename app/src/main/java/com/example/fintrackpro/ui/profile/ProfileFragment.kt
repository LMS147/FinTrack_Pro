package com.example.fintrackpro.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.fintrackpro.FinTrackApp
import com.example.fintrackpro.R
import com.example.fintrackpro.databinding.FragmentProfileBinding
import com.example.fintrackpro.ui.auth.LoginActivity
import com.example.fintrackpro.utils.PhotoHelper
import com.example.fintrackpro.utils.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val userId: String by lazy { SessionManager(requireContext()).getUserId() ?: "" }
    private val viewModel: ProfileViewModel by viewModels {
        val app = requireActivity().application as FinTrackApp
        ProfileViewModelFactory(app.userRepository, userId)
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val localUri = PhotoHelper.saveImageToInternalStorage(requireContext(), it)
            if (localUri != null) {
                viewModel.updateProfilePicture(localUri)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        setupListeners()
        observeState()
    }

    private fun setupListeners() {
        binding.ivAvatar.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.llCurrency.setOnClickListener {
            showCurrencyPicker()
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            // viewModel.toggleNotifications(isChecked)
        }

        binding.switchBiometrics.setOnCheckedChangeListener { _, isChecked ->
            // viewModel.toggleBiometrics(isChecked)
        }

        binding.btnLogout.setOnClickListener {
            SessionManager(requireContext()).clearSession()
            startActivity(Intent(requireContext(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            requireActivity().finish()
        }

        binding.llEditProfile.setOnClickListener {
            showEditProfileDialog()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    val user = state.user ?: return@collect

                    binding.tvDisplayName.text = user.fullName
                    binding.tvEmail.text = user.email
                    binding.tvCurrency.text = user.defaultCurrency
                    // binding.switchNotifications.isChecked = user.notificationsEnabled

                    user.profileImageUrl?.let {
                        try {
                            binding.ivAvatar.setImageURI(Uri.parse(it))
                        } catch (e: SecurityException) {
                            binding.ivAvatar.setImageResource(R.drawable.ic_profile)
                        }
                    } ?: run {
                        binding.ivAvatar.setImageResource(R.drawable.ic_profile)
                    }
                }
            }
        }
    }

    private fun showEditProfileDialog() {
        val user = viewModel.uiState.value.user ?: return
        
        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(64, 32, 64, 0)
        }

        val etName = EditText(requireContext()).apply {
            hint = "Full Name"
            setText(user.fullName)
        }
        val etEmail = EditText(requireContext()).apply {
            hint = "Email"
            setText(user.email)
        }

        layout.addView(etName)
        layout.addView(etEmail)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Edit Profile")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                viewModel.updateProfile(etName.text.toString(), etEmail.text.toString())
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showCurrencyPicker() {
        val currencies = arrayOf("ZAR", "USD", "EUR")
        val current = viewModel.uiState.value.user?.defaultCurrency ?: "ZAR"
        val checkedItem = currencies.indexOf(current).coerceAtLeast(0)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select Currency")
            .setSingleChoiceItems(currencies, checkedItem) { dialog, which ->
                val selected = currencies[which]
                viewModel.updateCurrency(selected)
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
