package com.avocado.glamping.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.glamping.data.model.resp.FacilityResponse
import com.avocado.glamping.data.model.resp.PagingResponse
import com.avocado.glamping.repository.FacilityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FacilityViewModel @Inject constructor(
    private val repository: FacilityRepository
) : ViewModel(){
    private val _getFacilities = MutableLiveData<GetFacilityState>()
    val getFacilities : LiveData<GetFacilityState> get() = _getFacilities

    fun getFacilities(
        params : Map<String, String>,
        page : Int,
        size : Int,
        fields : String,
        sortBy : String,
        direction : String
    ) {
        viewModelScope.launch {
            _getFacilities.value = GetFacilityState.Loading
            try {
                val response = repository.getFacilities(params,page,size,fields,sortBy,direction)
                response.onSuccess { resp ->
                    _getFacilities.value = GetFacilityState.Success(resp.data)
                }.onFailure { resp ->
                    _getFacilities.value = GetFacilityState.Error(resp.message.toString())
                }
            }catch (e : Exception){
                _getFacilities.value = GetFacilityState.Error(e.message.toString())
            }
        }
    }
}

sealed class GetFacilityState{
    data object Loading : GetFacilityState()
    data class Success(val response : PagingResponse<List<FacilityResponse>>) : GetFacilityState()
    data class Error(val mess : String) : GetFacilityState()
}