// UnitViewModel.kt
package com.tlu.tlucontact.ui.units

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tlu.tlucontact.data.model.OrganizationalUnit
import com.tlu.tlucontact.data.repository.UnitRepository
import kotlinx.coroutines.launch

class UnitViewModel : ViewModel() {
    private val unitRepository = UnitRepository()

    private val _units = MutableLiveData<List<OrganizationalUnit>>()
    val units: LiveData<List<OrganizationalUnit>> = _units

    private val _Organizational_unit = MutableLiveData<OrganizationalUnit>()
    val organizationalUnit: LiveData<OrganizationalUnit> = _Organizational_unit

    private val _childUnits = MutableLiveData<List<OrganizationalUnit>>()
    val childUnits: LiveData<List<OrganizationalUnit>> = _childUnits

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Get all units
    fun getAllUnits() {
        viewModelScope.launch {
            _loading.value = true
            val result = unitRepository.getAllUnits()
            _loading.value = false

            result.fold(
                onSuccess = { unitsList ->
                    _units.value = unitsList
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )
        }
    }

    // Get unit by ID
    fun getUnitById(unitId: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = unitRepository.getUnitById(unitId)
            _loading.value = false

            result.fold(
                onSuccess = { unitDetail ->
                    _Organizational_unit.value = unitDetail
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )
        }
    }

    // Search units by name
    fun searchUnitsByName(query: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = unitRepository.searchUnitsByName(query)
            _loading.value = false

            result.fold(
                onSuccess = { unitsList ->
                    _units.value = unitsList
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )
        }
    }

    // Filter units by type
    fun getUnitsByType(type: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = unitRepository.getUnitsByType(type)
            _loading.value = false

            result.fold(
                onSuccess = { unitsList ->
                    _units.value = unitsList
                },
                onFailure = { error ->
                    _errorMessage.value = error.message
                }
            )
        }
    }

    // Get child units
    fun getChildUnits(parentUnitId: String) {
        viewModelScope.launch {
            val result = unitRepository.getChildUnits(parentUnitId)

            result.fold(
                onSuccess = { unitsList ->
                    _childUnits.value = unitsList
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