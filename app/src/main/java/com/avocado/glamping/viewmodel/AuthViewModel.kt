package com.avocado.glamping.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.glamping.UserPreferences
import com.avocado.glamping.data.model.req.FcmTokenDeleteRequest
import com.avocado.glamping.data.model.resp.AuthResponse
import com.avocado.glamping.data.model.resp.BaseResponse
import com.avocado.glamping.data.model.resp.UserResponse
import com.avocado.glamping.repository.AuthRepository
import com.avocado.glamping.repository.FcmTokenRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val fcmTokenRepository : FcmTokenRepository,
) : ViewModel() {

    private val _signInIntent = MutableLiveData<IntentSenderRequest?>()
    val signInIntent: LiveData<IntentSenderRequest?> get() = _signInIntent

    private val _firebaseUser = MutableLiveData<FirebaseUser?>()
    val firebaseUser: LiveData<FirebaseUser?> get() = _firebaseUser

    private val _authResponse = MutableLiveData<BaseResponse<AuthResponse>?>()
    val authResponse: LiveData<BaseResponse<AuthResponse>?> get() = _authResponse

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _authState = MutableLiveData<AuthenticateState>()
    val authState : LiveData<AuthenticateState> get() = _authState

    private val _deleteFcmTokenState = MutableLiveData<DeleteFcmTokenState>()
    val deleteFcmTokenState : LiveData<DeleteFcmTokenState> get() = _deleteFcmTokenState

    fun signInWithGoogle() {
        repository.signInWithGoogle { intentSenderRequest ->
            _signInIntent.postValue(intentSenderRequest)
        }
    }

    fun handleSignInResult(data: Intent) {
        viewModelScope.launch {
            _authState.value = AuthenticateState.Loading
            val response = repository.handleGoogleSignInResult(data)
            response.onSuccess { resp ->
                _authResponse.postValue(resp)
                _authState.value = AuthenticateState.Success(resp.data)
            }.onFailure {
                _error.postValue("Authentication failed")
                _authState.value = AuthenticateState.Error("Authentication failed")
            }
        }
    }


    fun signOut(userId : Int?, context : Context) {
        viewModelScope.launch {
            Log.e("Fcm Delete", userId.toString())
            _deleteFcmTokenState.value = DeleteFcmTokenState.Loading
            val deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            Log.e("Fcm Delete", deviceId)
            val resp = fcmTokenRepository.deleteFcmToken(FcmTokenDeleteRequest(userId, deviceId))
            resp.onSuccess { resp ->
                Log.e("Fcm Delete", "Here 2")
                _deleteFcmTokenState.value = DeleteFcmTokenState.Success(resp.data)
            }.onFailure { resp ->
                Log.e("Fcm Delete", "Here 3")
                _deleteFcmTokenState.value = resp.message?.let { DeleteFcmTokenState.Error(it) }
            }
        }
        repository.signOut()
        _firebaseUser.postValue(null)
        _authResponse.postValue(null)
    }
}

sealed class AuthenticateState{
    data object Loading : AuthenticateState()
    data class Success(val response : AuthResponse) : AuthenticateState()
    data class Error(val mess : String) : AuthenticateState()
}
sealed class DeleteFcmTokenState{
    data object Loading : DeleteFcmTokenState()
    data class Success(val response : String) : DeleteFcmTokenState()
    data class Error(val mess : String) : DeleteFcmTokenState()
}
