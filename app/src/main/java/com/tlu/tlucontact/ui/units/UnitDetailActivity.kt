// UnitDetailActivity.kt
package com.tlu.tlucontact.ui.units

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.tlu.tlucontact.R
import com.tlu.tlucontact.databinding.ActivityUnitDetailBinding
import com.tlu.tlucontact.util.Constants
import com.tlu.tlucontact.util.ImageLoader

class UnitDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUnitDetailBinding
    private lateinit var viewModel: UnitViewModel
    private lateinit var childUnitsAdapter: UnitAdapter
    private var unitId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUnitDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = UnitViewModel()

        unitId = intent.getStringExtra(Constants.INTENT_UNIT_ID)

        if (unitId == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin đơn vị", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        setupRecyclerView()
        setupObservers()

        // Load unit and child units data
        viewModel.getUnitById(unitId!!)
        viewModel.getChildUnits(unitId!!)
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

    private fun setupRecyclerView() {
        childUnitsAdapter = UnitAdapter { unit ->
            // Navigate to child unit detail
            val intent = Intent(this, UnitDetailActivity::class.java)
            intent.putExtra(Constants.INTENT_UNIT_ID, unit.id)
            startActivity(intent)
        }

        binding.rvChildUnits.apply {
            layoutManager = LinearLayoutManager(this@UnitDetailActivity)
            adapter = childUnitsAdapter
        }
    }

    private fun setupObservers() {
        viewModel.organizationalUnit.observe(this) { unit ->
            // Set unit details
            binding.collapsingToolbar.title = unit.name
            binding.tvUnitName.text = unit.name
            binding.tvUnitCode.text = unit.code
            binding.tvUnitType.text = unit.type
            binding.tvUnitAddress.text = unit.address
            binding.tvUnitPhone.text = unit.phone
            binding.tvUnitEmail.text = unit.email

            // Set fax if available
            if (unit.fax.isNotEmpty()) {
                binding.tvUnitFax.text = unit.fax
                binding.layoutFax.visibility = View.VISIBLE
            } else {
                binding.layoutFax.visibility = View.GONE
            }

            // Load logo
            if (unit.logoURL.isNotEmpty()) {
                ImageLoader.loadImage(binding.ivUnitLogo, unit.logoURL, R.drawable.ic_unit)
            } else {
                binding.ivUnitLogo.setImageResource(R.drawable.ic_unit)
            }

            // Load header image or use a default background
            if (unit.logoURL.isNotEmpty()) {
                ImageLoader.loadImage(binding.ivUnitHeader, unit.logoURL, R.drawable.header_bg)
            }

            // Set up parent unit if exists
            if (unit.parentUnit != null) {
                binding.layoutParentUnit.visibility = View.VISIBLE

                unit.parentUnit.get().addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val parentName = document.getString("name") ?: ""
                        binding.tvParentUnit.text = parentName

                        binding.tvParentUnit.setOnClickListener {
                            // Navigate to parent unit
                            val intent = Intent(this, UnitDetailActivity::class.java)
                            intent.putExtra(Constants.INTENT_UNIT_ID, document.id)
                            startActivity(intent)
                        }
                    }
                }
            } else {
                binding.layoutParentUnit.visibility = View.GONE
            }

            showLoading(false)
        }

        viewModel.childUnits.observe(this) { childUnits ->
            childUnitsAdapter.setItems(childUnits)

            if (childUnits.isEmpty()) {
                binding.tvNoChildUnits.visibility = View.VISIBLE
            } else {
                binding.tvNoChildUnits.visibility = View.GONE
            }
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