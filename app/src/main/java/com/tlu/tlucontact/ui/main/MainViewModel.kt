// MainViewModel.kt
package com.tlu.tlucontact.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tlu.tlucontact.data.model.User
import com.tlu.tlucontact.data.repository.AuthRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            _loading.value = true
            val result = authRepository.getCurrentUser()
            _loading.value = false

            result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )
        }
    }

    fun logout() {
        authRepository.logout()
        _currentUser.value = null
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}