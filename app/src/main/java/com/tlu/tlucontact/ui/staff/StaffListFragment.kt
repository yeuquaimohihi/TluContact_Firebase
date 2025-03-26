// StaffListFragment.kt
package com.tlu.tlucontact.ui.staff

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.tlu.tlucontact.R
import com.tlu.tlucontact.databinding.FragmentStaffListBinding
import com.tlu.tlucontact.util.PreferenceManager

class StaffListFragment : Fragment() {

    private var _binding: FragmentStaffListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: StaffViewModel
    private lateinit var staffAdapter: StaffAdapter
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStaffListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[StaffViewModel::class.java]
        preferenceManager = PreferenceManager(requireContext())

        setupToolbar()
        setupRecyclerView()
        setupSearchView()
        setupFilterButtons()
        setupObservers()

        // Check if user is staff
        if (preferenceManager.isUserStaff()) {
            loadStaffData()
        } else {
            // Show access denied message for non-staff users
            binding.tvStaffAccessDenied.visibility = View.VISIBLE
            binding.searchFilterContainer.visibility = View.GONE
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        staffAdapter = StaffAdapter { staff ->
            // Navigate to staff detail
            val intent = Intent(requireContext(), StaffDetailActivity::class.java)
            intent.putExtra("staffId", staff.id)
            startActivity(intent)
        }

        binding.rvStaff.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = staffAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotEmpty()) {
                        viewModel.searchStaffByName(it)
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isEmpty()) {
                        loadStaffData()
                    }
                }
                return true
            }
        })
    }

    private fun setupFilterButtons() {
        binding.btnFilter.setOnClickListener {
            // Show filter dialog
            // TODO: Implement filter dialog
            Toast.makeText(requireContext(), "Filter feature coming soon", Toast.LENGTH_SHORT).show()
        }

        binding.btnSort.setOnClickListener {
            // Show sort dialog
            // TODO: Implement sort dialog
            Toast.makeText(requireContext(), "Sort feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        viewModel.staffList.observe(viewLifecycleOwner) { staffList ->
            staffAdapter.setItems(staffList)

            if (staffList.isEmpty()) {
                binding.tvNoStaff.visibility = View.VISIBLE
            } else {
                binding.tvNoStaff.visibility = View.GONE
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearErrorMessage()
            }
        }
    }

    private fun loadStaffData() {
        viewModel.getAllStaff()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}