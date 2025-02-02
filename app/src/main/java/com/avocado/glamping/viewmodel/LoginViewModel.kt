package com.avocado.glamping.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.glamping.data.model.resp.LoginResponse
import com.avocado.glamping.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel(){
    private val _loginState = MutableLiveData<LoginState>()
    val loginState : LiveData<LoginState> get() = _loginState

    fun login(email: String, password: String){
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val res = loginRepository.login(email, password)
                if (res.isSuccess){
                    _loginState.value = LoginState.Success(res.getOrNull()!!)
                }else{
                    _loginState.value = LoginState.Error("Login failed")
                }
            } catch (e : Exception){
                _loginState.value = LoginState.Error("Something went wrong")
            }
        }
    }
}

sealed class LoginState {
    data object Loading : LoginState()
    data class Success(val response : LoginResponse) : LoginState()
    data class Error(val message: String) : LoginState()
}