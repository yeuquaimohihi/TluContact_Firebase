// ProfileActivity.kt
package com.tlu.tlucontact.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.tlu.tlucontact.R
import com.tlu.tlucontact.databinding.ActivityProfileBinding
import com.tlu.tlucontact.util.PreferenceManager
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var preferenceManager: PreferenceManager
    private var selectedImageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                // Show selected image in ImageView
                Picasso.get()
                    .load(uri)
                    .into(binding.ivProfileAvatar)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        preferenceManager = PreferenceManager(this)

        setupToolbar()
        setupObservers()
        setupListeners()

        // Load profile data
        viewModel.loadUserProfile()
        showLoading(true)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupObservers() {
        // Observe user data
        viewModel.user.observe(this) { user ->
            binding.etFullName.setText(user.displayName)
            binding.etEmail.setText(user.email)
            binding.etPhone.setText(user.phoneNumber)

            // Load user avatar if available
            if (user.photoURL.isNotEmpty()) {
                Picasso.get()
                    .load(user.photoURL)
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .into(binding.ivProfileAvatar)
            }
        }

        // Observe student data (if user is a student)
        viewModel.student.observe(this) { student ->
            if (student != null) {
                binding.tilAddress.visibility = View.VISIBLE
                binding.etAddress.setText(student.address)
            }
        }

        // Observe loading state
        viewModel.loading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        // Observe error messages
        viewModel.errorMessage.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearErrorMessage()
            }
        }

        // Observe update success
        viewModel.updateSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, R.string.profile_update_success, Toast.LENGTH_SHORT).show()
                // Update name in preferences
                preferenceManager.saveUserName(binding.etFullName.text.toString())
                finish()
            }
        }
    }

    private fun setupListeners() {
        // Handle change photo button click
        binding.btnChangePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImage.launch(intent)
        }

        // Handle save button click
        binding.btnSave.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val address = binding.etAddress.text.toString().trim()

            // Validate inputs
            if (fullName.isEmpty()) {
                binding.tilFullName.error = getString(R.string.required_field)
                return@setOnClickListener
            }

            binding.tilFullName.error = null

            showLoading(true)

            // Upload image if selected
            selectedImageUri?.let {
                viewModel.uploadProfileImage(it)
            }

            // Update profile information
            viewModel.updateUserProfile(fullName, phone, address)
        }

        // Clear errors when editing
        binding.etFullName.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.tilFullName.error = null
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}