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
class UserViewModel @Inject constructor(
    private val repository : UserRepository
) : ViewModel(){
    private val _updateUserState = MutableLiveData<UpdateUserState>()
    val updateUserState : LiveData<UpdateUserState>get() = _updateUserState

    fun update(userId : Int, request : UserRequest){
        viewModelScope.launch {
            _updateUserState.value = UpdateUserState.Loading
            try {
                val resp = repository.updateUser(userId, request)
                resp.onSuccess { resp ->
                    _updateUserState.value = UpdateUserState.Success(resp.data)
                }.onFailure { resp ->
                    _updateUserState.value = resp.message?.let { UpdateUserState.Error(it) }
                }
            }catch (e : Exception){
                _updateUserState.value = UpdateUserState.Error(e.message.toString())
            }
        }
    }
}

sealed class UpdateUserState{
    data object Loading : UpdateUserState()
    data class Success(val response : UserResponse) : UpdateUserState()
    data class Error(val mess : String) : UpdateUserState()
}