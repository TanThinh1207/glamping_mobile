package com.avocado.glamping.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.glamping.data.model.resp.PagingResponse
import com.avocado.glamping.data.model.resp.UtilityResponse
import com.avocado.glamping.repository.UtilityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UtilityViewModel @Inject constructor(
    private val repository: UtilityRepository
) : ViewModel(){
    private val _getUtilitiesState = MutableLiveData<GetUtilitiesState>()
    val getUtilitiesState : LiveData<GetUtilitiesState>get() = _getUtilitiesState

    fun getUtilities(
        params : Map<String, String>,
        page : Int,
        size : Int,
        fields : String,
        sortBy : String,
        direction : String
    ) {
        viewModelScope.launch {
            _getUtilitiesState.value = GetUtilitiesState.Loading
            try {
                val response = repository.getUtilities(params,page,size,fields,sortBy,direction)
                response.onSuccess { resp ->
                    _getUtilitiesState.value = GetUtilitiesState.Success(resp.data)
                }.onFailure { resp ->
                    _getUtilitiesState.value = GetUtilitiesState.Error(resp.message.toString())
                }
            }catch (e : Exception){
                _getUtilitiesState.value = GetUtilitiesState.Error(e.message.toString())
            }
        }
    }
}

sealed class GetUtilitiesState{
    data object Loading : GetUtilitiesState()
    data class Success(val response : PagingResponse<List<UtilityResponse>>) : GetUtilitiesState()
    data class Error(val mess : String) : GetUtilitiesState()
}