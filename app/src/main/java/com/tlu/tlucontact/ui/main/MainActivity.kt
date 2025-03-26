// MainActivity.kt
package com.tlu.tlucontact.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.tlu.tlucontact.R
import com.tlu.tlucontact.data.model.DirectoryOption
import com.tlu.tlucontact.databinding.ActivityMainBinding
import com.tlu.tlucontact.ui.auth.LoginActivity
import com.tlu.tlucontact.ui.profile.ProfileActivity
import com.tlu.tlucontact.ui.staff.StaffListFragment
import com.tlu.tlucontact.ui.students.StudentListFragment
import com.tlu.tlucontact.ui.units.UnitListFragment
import com.tlu.tlucontact.util.PreferenceManager

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var directoryAdapter: DirectoryOptionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        preferenceManager = PreferenceManager(this)

        setupToolbar()
        setupNavDrawer()
        setupRecyclerView()
        loadUserData()
        setupObservers()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupNavDrawer() {
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)

        // Update nav header with user info
        val headerView = binding.navView.getHeaderView(0)
        val navUserName = headerView.findViewById<TextView>(R.id.tv_nav_user_name)
        val navUserEmail = headerView.findViewById<TextView>(R.id.tv_nav_user_email)

        navUserName.text = preferenceManager.getUserName()
        navUserEmail.text = preferenceManager.getUserEmail()
    }

    private fun setupRecyclerView() {
        directoryAdapter = DirectoryOptionAdapter { option ->
            when (option.id) {
                1 -> {
                    // Navigate to Unit Directory
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.drawer_layout, UnitListFragment())
                        .addToBackStack(null)
                        .commit()
                }
                2 -> {
                    // Navigate to Staff Directory (only if user is staff)
                    if (preferenceManager.isUserStaff()) {
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.drawer_layout, StaffListFragment())
                            .addToBackStack(null)
                            .commit()
                    } else {
                        Toast.makeText(
                            this,
                            R.string.staff_access_denied,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                3 -> {
                    // Navigate to Student Directory
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.drawer_layout, StudentListFragment())
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        binding.rvDirectoryOptions.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = directoryAdapter
        }

        // Load directory options
        val options = mutableListOf<DirectoryOption>()
        options.add(
            DirectoryOption(
                1,
                getString(R.string.unit_directory),
                getString(R.string.unit_directory_desc),
                R.drawable.ic_unit
            )
        )
        options.add(
            DirectoryOption(
                2,
                getString(R.string.staff_directory),
                getString(R.string.staff_directory_desc),
                R.drawable.ic_staff
            )
        )
        options.add(
            DirectoryOption(
                3,
                getString(R.string.student_directory),
                getString(R.string.student_directory_desc),
                R.drawable.ic_student
            )
        )

        directoryAdapter.setItems(options)
    }

    private fun loadUserData() {
        // Display user info
        binding.tvUserName.text = preferenceManager.getUserName()
        binding.tvUserRole.text = if (preferenceManager.isUserStaff())
            getString(R.string.role_staff) else getString(R.string.role_student)

        // Load full user data from ViewModel
        viewModel.getCurrentUser()
    }

    private fun setupObservers() {
        viewModel.currentUser.observe(this) { user ->
            if (user != null) {
                // Update user avatar if available
                if (user.photoURL.isNotEmpty()) {
                    Glide.with(this)
                        .load(user.photoURL)
                        .placeholder(R.drawable.default_avatar)
                        .error(R.drawable.default_avatar)
                        .into(binding.ivUserAvatar)

                    // Also update nav drawer header
                    val headerView = binding.navView.getHeaderView(0)
                    val navUserAvatar = headerView.findViewById<ImageView>(R.id.iv_nav_user_avatar)
                    Glide.with(this)
                        .load(user.photoURL)
                        .placeholder(R.drawable.default_avatar)
                        .error(R.drawable.default_avatar)
                        .into(navUserAvatar)
                }
            }
        }

        viewModel.loading.observe(this) { isLoading ->
            // Could show a progress indicator if needed
        }

        viewModel.errorMessage.observe(this) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearErrorMessage()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
            }
            R.id.nav_unit_directory -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.drawer_layout, UnitListFragment())
                    .addToBackStack(null)
                    .commit()
            }
            R.id.nav_staff_directory -> {
                if (preferenceManager.isUserStaff()) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.drawer_layout, StaffListFragment())
                        .addToBackStack(null)
                        .commit()
                } else {
                    Toast.makeText(
                        this,
                        R.string.staff_access_denied,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            R.id.nav_student_directory -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.drawer_layout, StudentListFragment())
                    .addToBackStack(null)
                    .commit()
            }
            R.id.nav_logout -> {
                viewModel.logout()
                preferenceManager.clearPreferences()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}