// StudentListFragment.kt
package com.tlu.tlucontact.ui.student

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
import com.tlu.tlucontact.databinding.FragmentStudentListBinding
import com.tlu.tlucontact.util.PreferenceManager

class StudentListFragment : Fragment() {

    private var _binding: FragmentStudentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: StudentViewModel
    private lateinit var studentAdapter: StudentAdapter
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[StudentViewModel::class.java]
        preferenceManager = PreferenceManager(requireContext())

        setupToolbar()
        setupRecyclerView()
        setupSearchView()
        setupFilterButtons()
        setupObservers()

        // Check if user is staff or student
        if (preferenceManager.isUserStaff() ) {
            loadStudentData()
        } else {
            // Show access denied message for guests
            binding.tvStudentAccessDenied.visibility = View.VISIBLE
            binding.searchFilterContainer.visibility = View.GONE
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        studentAdapter = StudentAdapter { student ->
            // Navigate to student detail
            val intent = Intent(requireContext(), StudentDetailActivity::class.java)
            intent.putExtra("studentId", student.id)
            startActivity(intent)
        }

        binding.rvStudents.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = studentAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotEmpty()) {
                        viewModel.searchStudentsByName(it)
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isEmpty()) {
                        loadStudentData()
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
        viewModel.studentList.observe(viewLifecycleOwner) { studentList ->
            studentAdapter.setItems(studentList)

            if (studentList.isEmpty()) {
                binding.tvNoStudents.visibility = View.VISIBLE
            } else {
                binding.tvNoStudents.visibility = View.GONE
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

    private fun loadStudentData() {
        viewModel.getAllStudents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}