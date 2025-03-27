package com.avocado.glamping.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.glamping.data.model.req.ServiceUpdateRequest
import com.avocado.glamping.data.model.resp.SelectionResponse
import com.avocado.glamping.repository.SelectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectionViewModel @Inject constructor(
    private val repository : SelectionRepository
)  : ViewModel(){

    private val _updateSelectionState = MutableLiveData<UpdateSelectionState>()
    val updateSelectionState : LiveData<UpdateSelectionState> get() = _updateSelectionState

    fun updateSelection(
        request : ServiceUpdateRequest
    ) {
        viewModelScope.launch {
            _updateSelectionState.value = UpdateSelectionState.Loading
            try {
                val response = repository.updateSelection(request)
                response.onSuccess { resp ->
                    _updateSelectionState.value = UpdateSelectionState.Success(resp.data)
                }.onFailure { resp ->
                    _updateSelectionState.value = UpdateSelectionState.Error(resp.message.toString())
                }
            }catch (e : Exception){
                _updateSelectionState.value = UpdateSelectionState.Error(e.message.toString())
            }
        }
    }

}

sealed class UpdateSelectionState {
    data object Loading : UpdateSelectionState()
    data class Success (val response : SelectionResponse) : UpdateSelectionState()
    data class Error (val mess : String) : UpdateSelectionState()
}