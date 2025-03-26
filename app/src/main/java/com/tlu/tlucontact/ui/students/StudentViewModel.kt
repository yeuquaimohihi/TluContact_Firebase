// StudentViewModel.kt
package com.tlu.tlucontact.ui.student

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tlu.tlucontact.data.model.Student
import com.tlu.tlucontact.data.repository.StudentRepository
import kotlinx.coroutines.launch

class StudentViewModel : ViewModel() {
    private val studentRepository = StudentRepository()

    private val _studentList = MutableLiveData<List<Student>>()
    val studentList: LiveData<List<Student>> = _studentList

    private val _student = MutableLiveData<Student>()
    val student: LiveData<Student> = _student

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Get all students
    fun getAllStudents() {
        viewModelScope.launch {
            _loading.value = true
            val result = studentRepository.getAllStudents()
            _loading.value = false

            result.fold(
                onSuccess = { studentsList ->
                    _studentList.value = studentsList
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )
        }
    }

    // Get student by ID
    fun getStudentById(studentId: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = studentRepository.getStudentById(studentId)
            _loading.value = false

            result.fold(
                onSuccess = { studentDetail ->
                    _student.value = studentDetail
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )
        }
    }

    // Get student by user ID
    fun getStudentByUserId(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = studentRepository.getStudentByUserId(userId)
            _loading.value = false

            result.fold(
                onSuccess = { studentDetail ->
                    _student.value = studentDetail
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )
        }
    }

    // Search students by name
    fun searchStudentsByName(query: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = studentRepository.searchStudentsByName(query)
            _loading.value = false

            result.fold(
                onSuccess = { studentsList ->
                    _studentList.value = studentsList
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )
        }
    }

    // Filter students by class
    fun getStudentsByClass(classId: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = studentRepository.getStudentsByClass(classId)
            _loading.value = false

            result.fold(
                onSuccess = { studentsList ->
                    _studentList.value = studentsList
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )
        }
    }

    // Update student profile
    fun updateStudentProfile(student: Student) {
        viewModelScope.launch {
            _loading.value = true
            val result = studentRepository.updateStudentProfile(student)
            _loading.value = false

            result.fold(
                onSuccess = { updatedStudent ->
                    _student.value = updatedStudent
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}