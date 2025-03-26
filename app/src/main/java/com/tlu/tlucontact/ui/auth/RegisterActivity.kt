// RegisterActivity.kt
package com.tlu.tlucontact.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.tlu.tlucontact.databinding.ActivityRegisterBinding
import com.tlu.tlucontact.ui.main.MainActivity
import com.tlu.tlucontact.util.Constants
import com.tlu.tlucontact.util.PreferenceManager

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        preferenceManager = PreferenceManager(this)

        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.registrationResult.observe(this) { result ->
            binding.progressBar.visibility = View.GONE

            result.fold(
                onSuccess = { user ->
                    // Save user info to preferences
                    preferenceManager.saveUserId(user.id)
                    preferenceManager.saveUserRole(user.role)
                    preferenceManager.saveUserName(user.displayName)
                    preferenceManager.saveUserEmail(user.email)

                    // Show success message
                    Toast.makeText(
                        this,
                        "Đăng ký thành công. Vai trò của bạn: ${if (user.role == "CBGV") "Cán bộ/Giảng viên" else "Sinh viên"}",
                        Toast.LENGTH_LONG
                    ).show()

                    // Navigate to MainActivity
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                },
                onFailure = { exception ->
                    Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnRegister.setOnClickListener {
            val fullName = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            // Validate inputs
            when {
                fullName.isEmpty() -> {
                    binding.tilName.error = Constants.ERROR_NAME_REQUIRED
                    return@setOnClickListener
                }
                email.isEmpty() -> {
                    binding.tilEmail.error = Constants.ERROR_EMAIL_REQUIRED
                    return@setOnClickListener
                }
                password.isEmpty() -> {
                    binding.tilPassword.error = Constants.ERROR_PASSWORD_REQUIRED
                    return@setOnClickListener
                }
                password.length < 6 -> {
                    binding.tilPassword.error = Constants.ERROR_PASSWORD_LENGTH
                    return@setOnClickListener
                }
                password != confirmPassword -> {
                    binding.tilConfirmPassword.error = Constants.ERROR_PASSWORD_MATCH
                    return@setOnClickListener
                }
            }

            // Clear errors
            binding.tilName.error = null
            binding.tilEmail.error = null
            binding.tilPassword.error = null
            binding.tilConfirmPassword.error = null

            // Register user
            binding.progressBar.visibility = View.VISIBLE
            viewModel.register(email, password, fullName)
        }

        binding.tvLogin.setOnClickListener {
            onBackPressed()
        }

        // Clear errors on focus
        binding.etName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilName.error = null
        }

        binding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilEmail.error = null
        }

        binding.etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilPassword.error = null
        }

        binding.etConfirmPassword.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilConfirmPassword.error = null
        }
    }
}