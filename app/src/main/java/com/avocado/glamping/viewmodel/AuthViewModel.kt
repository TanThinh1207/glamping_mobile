// AuthViewModel.kt
package com.avocado.glamping.viewmodel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avocado.glamping.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.auth.api.identity.BeginSignInResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository : AuthRepository
) : ViewModel() {
    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> get() = _user

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _signInIntent = MutableLiveData<IntentSenderRequest?>()
    val signInIntent: LiveData<IntentSenderRequest?> get() = _signInIntent


    fun signInWithGoogle() {
        repository.signInWithGoogle { intentSenderRequest ->
            _signInIntent.postValue(intentSenderRequest)
        }
    }

    fun handleSignInResult(data : Intent){
        repository.handleGoogleSignInResult(data){ user ->
            _user.postValue(user)
            if (user == null) _error.postValue("Authentication failed")
        }
    }

    fun signOut() {
        repository.signOut()
        _user.postValue(null)
    }
}
