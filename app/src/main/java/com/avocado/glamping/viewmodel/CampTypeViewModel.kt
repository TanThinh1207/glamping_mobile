package com.avocado.glamping.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.glamping.data.model.req.CampTypeUpdateRequest
import com.avocado.glamping.data.model.resp.CampTypeResponse
import com.avocado.glamping.repository.CampTypeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CampTypeViewModel @Inject constructor(
    private val repository : CampTypeRepository
) : ViewModel(){
    private val _updateCampTypeState = MutableLiveData<UpdateCampTypeState>()
    val updateCampTypeState : LiveData<UpdateCampTypeState>get() = _updateCampTypeState

    private val _updateCampTypeFacilitiesState = MutableLiveData<UpdateCampTypeFacilitiesState>()
    val updateCampTypeFacilitiesState : LiveData<UpdateCampTypeFacilitiesState>get() = _updateCampTypeFacilitiesState

    fun updateCampType(request : CampTypeUpdateRequest, id : Int){
        viewModelScope.launch {
            _updateCampTypeState.value = UpdateCampTypeState.Loading
            try {
                val response = repository.updateCampType(request, id)
                response.onSuccess { resp ->
                    _updateCampTypeState.value = UpdateCampTypeState.Success(resp.data)
                }.onFailure { resp ->
                    _updateCampTypeState.value = UpdateCampTypeState.Error(resp.message.toString())
                }
            }catch (e : Exception){
                _updateCampTypeState.value = UpdateCampTypeState.Error(e.message.toString())
            }
        }
    }

    fun updateCampTypeFacilities(campTypeId : Int, ids : List<Int>){
        viewModelScope.launch {
            _updateCampTypeFacilitiesState.value = UpdateCampTypeFacilitiesState.Loading
            try {
                val response = repository.updateCampTypeFacilities(campTypeId, ids)
                response.onSuccess { resp ->
                    _updateCampTypeFacilitiesState.value = UpdateCampTypeFacilitiesState.Success(resp.data)
                }.onFailure { resp ->
                    _updateCampTypeFacilitiesState.value = UpdateCampTypeFacilitiesState.Error(resp.message.toString())
                }
            }catch (e : Exception){
                _updateCampTypeFacilitiesState.value = UpdateCampTypeFacilitiesState.Error(e.message.toString())
            }
        }
    }
}

sealed class UpdateCampTypeState(){
    data object Loading : UpdateCampTypeState()
    data class Success(val response : CampTypeResponse) : UpdateCampTypeState()
    data class Error(val mess : String) : UpdateCampTypeState()
}

sealed class UpdateCampTypeFacilitiesState(){
    data object Loading : UpdateCampTypeFacilitiesState()
    data class Success(val response : CampTypeResponse) : UpdateCampTypeFacilitiesState()
    data class Error(val mess : String) : UpdateCampTypeFacilitiesState()
}