// StaffDetailActivity.kt
package com.tlu.tlucontact.ui.staff

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.tlu.tlucontact.R
import com.tlu.tlucontact.databinding.ActivityStaffDetailBinding
import com.tlu.tlucontact.ui.units.UnitDetailActivity
import com.tlu.tlucontact.util.Constants
import com.tlu.tlucontact.util.ImageLoader

class StaffDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStaffDetailBinding
    private lateinit var viewModel: StaffViewModel
    private var staffId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[StaffViewModel::class.java]

        staffId = intent.getStringExtra(Constants.INTENT_STAFF_ID)

        if (staffId == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin cán bộ", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        setupObservers()

        // Load staff data
        viewModel.getStaffById(staffId!!)
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
        viewModel.staff.observe(this) { staff ->
            // Set staff details
            binding.collapsingToolbar.title = staff.fullName
            binding.tvStaffName.text = staff.fullName
            binding.tvStaffPosition.text = staff.position
            binding.tvStaffId.text = staff.staffId
            binding.tvStaffPhone.text = staff.phone
            binding.tvStaffEmail.text = staff.email

            // Load avatar
            if (staff.photoURL.isNotEmpty()) {
                ImageLoader.loadImage(binding.ivStaffAvatar, staff.photoURL, R.drawable.default_avatar)
            }

            // Set up unit click listener
            staff.unit?.get()?.addOnSuccessListener { documentSnapshot ->
                val unitName = documentSnapshot.getString("name") ?: ""
                binding.tvStaffUnit.text = unitName

                binding.tvStaffUnit.setOnClickListener {
                    // Navigate to unit detail
                    val intent = Intent(this, UnitDetailActivity::class.java)
                    intent.putExtra(Constants.INTENT_UNIT_ID, staff.unit?.id)
                    startActivity(intent)
                }
            }

            showLoading(false)
        }

        viewModel.loading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.errorMessage.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearErrorMessage()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}