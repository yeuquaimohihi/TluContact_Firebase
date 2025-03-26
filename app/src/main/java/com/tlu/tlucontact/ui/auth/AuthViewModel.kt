// AuthViewModel.kt
package com.tlu.tlucontact.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tlu.tlucontact.data.model.User
import com.tlu.tlucontact.data.repository.AuthRepository
import com.tlu.tlucontact.util.Constants
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _registrationResult = MutableLiveData<Result<User>>()
    val registrationResult: LiveData<Result<User>> = _registrationResult

    private val _loginResult = MutableLiveData<Result<User>>()
    val loginResult: LiveData<Result<User>> = _loginResult

    private val _currentUser = MutableLiveData<Result<User>>()
    val currentUser: LiveData<Result<User>> = _currentUser

    // Đăng ký
    fun register(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            // Validate input
            when {
                email.isEmpty() -> {
                    _registrationResult.value = Result.failure(Exception(Constants.ERROR_EMAIL_REQUIRED))
                    return@launch
                }
                !isValidEmail(email) -> {
                    _registrationResult.value = Result.failure(Exception(Constants.ERROR_INVALID_EMAIL))
                    return@launch
                }
                password.isEmpty() -> {
                    _registrationResult.value = Result.failure(Exception(Constants.ERROR_PASSWORD_REQUIRED))
                    return@launch
                }
                password.length < 6 -> {
                    _registrationResult.value = Result.failure(Exception(Constants.ERROR_PASSWORD_LENGTH))
                    return@launch
                }
                displayName.isEmpty() -> {
                    _registrationResult.value = Result.failure(Exception(Constants.ERROR_NAME_REQUIRED))
                    return@launch
                }
            }

            _registrationResult.value = authRepository.register(email, password, displayName)
        }
    }

    // Đăng nhập
    fun login(email: String, password: String) {
        viewModelScope.launch {
            // Validate input
            when {
                email.isEmpty() -> {
                    _loginResult.value = Result.failure(Exception(Constants.ERROR_EMAIL_REQUIRED))
                    return@launch
                }
                password.isEmpty() -> {
                    _loginResult.value = Result.failure(Exception(Constants.ERROR_PASSWORD_REQUIRED))
                    return@launch
                }
            }

            _loginResult.value = authRepository.login(email, password)
        }
    }

    // Đăng xuất
    fun logout() {
        authRepository.logout()
    }

    // Lấy thông tin người dùng hiện tại
    fun getCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = authRepository.getCurrentUser()
        }
    }

    // Kiểm tra đã đăng nhập chưa
    fun isLoggedIn() = authRepository.isLoggedIn()

    // Kiểm tra email hợp lệ
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                (email.endsWith(Constants.EMAIL_PATTERN_STAFF) ||
                        email.endsWith(Constants.EMAIL_PATTERN_STUDENT))
    }
}