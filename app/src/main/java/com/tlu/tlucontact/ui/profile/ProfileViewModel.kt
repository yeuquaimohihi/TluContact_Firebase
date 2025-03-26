// ProfileViewModel.kt
package com.tlu.tlucontact.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tlu.tlucontact.data.model.Staff
import com.tlu.tlucontact.data.model.Student
import com.tlu.tlucontact.data.model.User
import com.tlu.tlucontact.data.repository.AuthRepository
import com.tlu.tlucontact.data.repository.StaffRepository
import com.tlu.tlucontact.data.repository.StudentRepository
import com.tlu.tlucontact.util.FirebaseUtils
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    private val staffRepository = StaffRepository()
    private val studentRepository = StudentRepository()

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _staff = MutableLiveData<Staff?>()
    val staff: LiveData<Staff?> = _staff

    private val _student = MutableLiveData<Student?>()
    val student: LiveData<Student?> = _student

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> = _updateSuccess

    // Lấy thông tin người dùng hiện tại
    fun loadUserProfile() {
        viewModelScope.launch {
            _loading.value = true

            val userResult = authRepository.getCurrentUser()

            userResult.fold(
                onSuccess = { user ->
                    _user.value = user

                    // Lấy thông tin chi tiết theo role
                    if (user.isStaff()) {
                        loadStaffDetails(user.id)
                    } else if (user.isStudent()) {
                        loadStudentDetails(user.id)
                    }
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                    _loading.value = false
                }
            )
        }
    }

    // Lấy thông tin chi tiết CBGV
    private suspend fun loadStaffDetails(userId: String) {
        val staffResult = staffRepository.getStaffByUserId(userId)

        staffResult.fold(
            onSuccess = { staffDetail ->
                _staff.value = staffDetail
            },
            onFailure = { error ->
                _errorMessage.value = error.message
            }
        )

        _loading.value = false
    }

    // Lấy thông tin chi tiết sinh viên
    private suspend fun loadStudentDetails(userId: String) {
        val studentResult = studentRepository.getStudentByUserId(userId)

        studentResult.fold(
            onSuccess = { studentDetail ->
                _student.value = studentDetail
            },
            onFailure = { error ->
                _errorMessage.value = error.message
            }
        )

        _loading.value = false
    }

    // Upload ảnh đại diện
    fun uploadProfileImage(imageUri: Uri) {
        _user.value?.let { user ->
            viewModelScope.launch {
                _loading.value = true

                try {
                    val imageUrl = FirebaseUtils.uploadImage(imageUri, "users/${user.id}")
                    updateUserWithPhoto(imageUrl)
                } catch (e: Exception) {
                    _errorMessage.value = "Lỗi tải ảnh lên: ${e.message}"
                    _loading.value = false
                }
            }
        }
    }

    // Cập nhật ảnh đại diện cho user
    private suspend fun updateUserWithPhoto(photoUrl: String) {
        _user.value?.let { currentUser ->
            val updatedUser = currentUser.copy(photoURL = photoUrl)

            val result = authRepository.updateUserProfile(updatedUser)

            result.fold(
                onSuccess = {
                    _user.value = updatedUser

                    // Cập nhật ảnh trong thông tin chi tiết
                    if (currentUser.isStaff()) {
                        updateStaffPhoto(photoUrl)
                    } else if (currentUser.isStudent()) {
                        updateStudentPhoto(photoUrl)
                    }
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                    _loading.value = false
                }
            )
        }
    }

    // Cập nhật ảnh đại diện cho CBGV
    private suspend fun updateStaffPhoto(photoUrl: String) {
        _staff.value?.let { currentStaff ->
            val updatedStaff = currentStaff.copy(photoURL = photoUrl)

            val result = staffRepository.updateStaffProfile(updatedStaff)

            result.fold(
                onSuccess = {
                    _staff.value = updatedStaff
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )

            _loading.value = false
        }
    }

    // Cập nhật ảnh đại diện cho sinh viên
    private suspend fun updateStudentPhoto(photoUrl: String) {
        _student.value?.let { currentStudent ->
            val updatedStudent = currentStudent.copy(photoURL = photoUrl)

            val result = studentRepository.updateStudentProfile(updatedStudent)

            result.fold(
                onSuccess = {
                    _student.value = updatedStudent
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )

            _loading.value = false
        }
    }

    // Cập nhật thông tin người dùng
    fun updateUserProfile(displayName: String, phoneNumber: String, address: String? = null) {
        _user.value?.let { currentUser ->
            viewModelScope.launch {
                _loading.value = true

                // Cập nhật thông tin User
                val updatedUser = currentUser.copy(
                    displayName = displayName,
                    phoneNumber = phoneNumber
                )

                val userResult = authRepository.updateUserProfile(updatedUser)

                userResult.fold(
                    onSuccess = {
                        _user.value = updatedUser

                        // Cập nhật thông tin chi tiết theo role
                        if (currentUser.isStaff()) {
                            updateStaffProfile(displayName, phoneNumber)
                        } else if (currentUser.isStudent()) {
                            updateStudentProfile(displayName, phoneNumber, address)
                        }
                    },
                    onFailure = { error ->
                        _errorMessage.value = error.message
                        _loading.value = false
                        _updateSuccess.value = false
                    }
                )
            }
        }
    }

    // Cập nhật thông tin CBGV
    private suspend fun updateStaffProfile(fullName: String, phone: String) {
        _staff.value?.let { currentStaff ->
            val updatedStaff = currentStaff.copy(
                fullName = fullName,
                phone = phone
            )

            val result = staffRepository.updateStaffProfile(updatedStaff)

            result.fold(
                onSuccess = {
                    _staff.value = updatedStaff
                    _updateSuccess.value = true
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                    _updateSuccess.value = false
                }
            )

            _loading.value = false
        }
    }

    // Cập nhật thông tin sinh viên
    private suspend fun updateStudentProfile(fullName: String, phone: String, address: String?) {
        _student.value?.let { currentStudent ->
            val updatedStudent = currentStudent.copy(
                fullName = fullName,
                phone = phone,
                address = address ?: currentStudent.address
            )

            val result = studentRepository.updateStudentProfile(updatedStudent)

            result.fold(
                onSuccess = {
                    _student.value = updatedStudent
                    _updateSuccess.value = true
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                    _updateSuccess.value = false
                }
            )

            _loading.value = false
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}