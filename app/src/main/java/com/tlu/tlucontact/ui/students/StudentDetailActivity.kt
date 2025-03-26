// StudentDetailActivity.kt
package com.tlu.tlucontact.ui.student

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.tlu.tlucontact.R
import com.tlu.tlucontact.databinding.ActivityStudentDetailBinding
import com.tlu.tlucontact.ui.units.UnitDetailActivity
import com.tlu.tlucontact.util.Constants
import com.tlu.tlucontact.util.ImageLoader

class StudentDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentDetailBinding
    private lateinit var viewModel: StudentViewModel
    private var studentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[StudentViewModel::class.java]

        studentId = intent.getStringExtra("studentId")

        if (studentId == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin sinh viên", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupToolbar()
        setupObservers()

        // Load student data
        viewModel.getStudentById(studentId!!)
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
        // Inside StudentDetailActivity.kt, in the setupObservers method
        viewModel.student.observe(this) { student ->
            // Set student details
            binding.collapsingToolbar.title = student.fullName
            binding.tvStudentName.text = student.fullName
            binding.tvStudentId.text = student.studentId
            binding.tvStudentPhone.text = student.phone
            binding.tvStudentEmail.text = student.email
            binding.tvStudentAddress.text = student.address

            // Load avatar
            if (student.photoURL.isNotEmpty()) {
                ImageLoader.loadImage(binding.ivStudentAvatar, student.photoURL, R.drawable.default_avatar)
            }

            // Set up class click listener
            if (student.classInfo != null) {
                student.classInfo.get().addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        val className = documentSnapshot.getString("name") ?: "N/A"
                        binding.tvStudentClass.text = className

                        // Additional class info
                        val classId = documentSnapshot.id
                        val facultyRef = documentSnapshot.getDocumentReference("faculty")

                        // Set up faculty click listener if available
                        if (facultyRef != null) {
                            facultyRef.get().addOnSuccessListener { facultyDoc ->
                                if (facultyDoc != null && facultyDoc.exists()) {
                                    val facultyName = facultyDoc.getString("name") ?: "N/A"
                                    binding.tvStudentFaculty.text = facultyName

                                    binding.tvStudentFaculty.setOnClickListener {
                                        // Navigate to faculty detail (unit detail)
                                        val intent = Intent(this, UnitDetailActivity::class.java)
                                        intent.putExtra(Constants.INTENT_UNIT_ID, facultyRef.id)
                                        startActivity(intent)
                                    }
                                } else {
                                    binding.tvStudentFaculty.text = "N/A"
                                }
                            }.addOnFailureListener {
                                binding.tvStudentFaculty.text = "N/A"
                            }
                        } else {
                            binding.tvStudentFaculty.text = "N/A"
                        }
                    } else {
                        binding.tvStudentClass.text = "N/A"
                        binding.tvStudentFaculty.text = "N/A"
                    }
                }.addOnFailureListener {
                    binding.tvStudentClass.text = "N/A"
                    binding.tvStudentFaculty.text = "N/A"
                }
            } else {
                binding.tvStudentClass.text = "N/A"
                binding.tvStudentFaculty.text = "N/A"
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