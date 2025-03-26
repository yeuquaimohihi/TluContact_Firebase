// UnitListFragment.kt
package com.tlu.tlucontact.ui.units

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tlu.tlucontact.databinding.FragmentUnitListBinding
import com.tlu.tlucontact.util.Constants

class UnitListFragment : Fragment() {

    private var _binding: FragmentUnitListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UnitViewModel
    private lateinit var unitAdapter: UnitAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUnitListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = UnitViewModel()

        setupToolbar()
        setupRecyclerView()
        setupSearchView()
        setupFilterButtons()
        setupObservers()

        // Load initial data
        viewModel.getAllUnits()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        unitAdapter = UnitAdapter { unit ->
            // Navigate to unit detail
            val intent = Intent(requireContext(), UnitDetailActivity::class.java)
            intent.putExtra(Constants.INTENT_UNIT_ID, unit.id)
            startActivity(intent)
        }

        binding.rvUnits.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = unitAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotEmpty()) {
                        viewModel.searchUnitsByName(it)
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isEmpty()) {
                        viewModel.getAllUnits()
                    }
                }
                return true
            }
        })
    }

    private fun setupFilterButtons() {
        binding.btnFilter.setOnClickListener {
            // Show filter dialog with unit types
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
        viewModel.units.observe(viewLifecycleOwner) { units ->
            unitAdapter.setItems(units)

            if (units.isEmpty()) {
                binding.tvNoUnits.visibility = View.VISIBLE
            } else {
                binding.tvNoUnits.visibility = View.GONE
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}