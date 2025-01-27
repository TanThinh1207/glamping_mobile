package com.avocado.glamping.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.glamping.data.model.resp.RegisterResponse
import com.avocado.glamping.repository.RegisterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerRepository: RegisterRepository
) : ViewModel() {
    private val _registerState = MutableLiveData<RegisterState>()
    val registerState : LiveData<RegisterState> get() = _registerState

    fun register(firstName : String, lastName : String, email : String, password : String){
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                val resp = registerRepository.register(firstName,lastName,email,password)
                if (resp.isSuccess){
                    _registerState.value = RegisterState.Success(resp.getOrNull()!!)
                }else {
                    _registerState.value = RegisterState.Error("Register failed")
                }
            }catch (e : Exception){
                _registerState.value = RegisterState.Error(e.message.toString())
            }
        }
    }

}

sealed class RegisterState{
    data object Loading : RegisterState()
    data class Success(val response : RegisterResponse) : RegisterState()
    data class Error(val mess : String) : RegisterState()
}