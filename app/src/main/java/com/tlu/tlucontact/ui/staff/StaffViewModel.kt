// StaffViewModel.kt
package com.tlu.tlucontact.ui.staff

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tlu.tlucontact.data.model.Staff
import com.tlu.tlucontact.data.repository.StaffRepository
import kotlinx.coroutines.launch

class StaffViewModel : ViewModel() {
    private val staffRepository = StaffRepository()

    private val _staffList = MutableLiveData<List<Staff>>()
    val staffList: LiveData<List<Staff>> = _staffList

    private val _staff = MutableLiveData<Staff>()
    val staff: LiveData<Staff> = _staff

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Lấy tất cả CBGV
    fun getAllStaff() {
        viewModelScope.launch {
            _loading.value = true
            val result = staffRepository.getAllStaff()
            _loading.value = false

            result.fold(
                onSuccess = { list ->
                    _staffList.value = list
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )
        }
    }

    // Lấy chi tiết CBGV
    fun getStaffById(staffId: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = staffRepository.getStaffById(staffId)
            _loading.value = false

            result.fold(
                onSuccess = { staffDetail ->
                    _staff.value = staffDetail
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )
        }
    }

    // Lấy CBGV theo userId
    fun getStaffByUserId(userId: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = staffRepository.getStaffByUserId(userId)
            _loading.value = false

            result.fold(
                onSuccess = { staffDetail ->
                    _staff.value = staffDetail
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )
        }
    }

    // Tìm kiếm CBGV theo tên
    fun searchStaffByName(query: String) {
        if (query.isNotEmpty()) {
            viewModelScope.launch {
                _loading.value = true
                val result = staffRepository.searchStaffByName(query)
                _loading.value = false

                result.fold(
                    onSuccess = { list ->
                        _staffList.value = list
                    },
                    onFailure = { error ->
                        _errorMessage.value = error.message
                    }
                )
            }
        } else {
            getAllStaff()
        }
    }

    // Lấy CBGV theo đơn vị
    fun getStaffByUnit(unitId: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = staffRepository.getStaffByUnit(unitId)
            _loading.value = false

            result.fold(
                onSuccess = { list ->
                    _staffList.value = list
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )
        }
    }

    // Cập nhật thông tin cá nhân CBGV
    fun updateStaffProfile(staff: Staff) {
        viewModelScope.launch {
            _loading.value = true
            val result = staffRepository.updateStaffProfile(staff)
            _loading.value = false

            result.fold(
                onSuccess = {
                    _staff.value = staff
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