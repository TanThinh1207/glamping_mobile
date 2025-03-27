package com.avocado.glamping.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.glamping.data.model.req.UserRequest
import com.avocado.glamping.data.model.resp.UserResponse
import com.avocado.glamping.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository : UserRepository
) : ViewModel(){
    private val _registerState = MutableLiveData<RegisterState>()
    val registerState : LiveData<RegisterState> get() = _registerState

    fun update(userId : Int, request : UserRequest){
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                val resp = repository.updateUser(userId, request)
                resp.onSuccess { resp ->
                    _registerState.value = RegisterState.Success(resp.data)
                }.onFailure { resp ->
                    _registerState.value = resp.message?.let { RegisterState.Error(it) }
                }
            }catch (e : Exception){
                _registerState.value = RegisterState.Error(e.message.toString())
            }
        }
    }
}

sealed class RegisterState{
    data object Loading : RegisterState()
    data class Success(val response : UserResponse) : RegisterState()
    data class Error(val mess : String) : RegisterState()
}