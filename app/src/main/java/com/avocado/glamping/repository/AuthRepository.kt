package com.avocado.glamping.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AuthRepository @Inject constructor(@ApplicationContext private val context : Context) {
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    private val signInClient : SignInClient = Identity.getSignInClient(context)

    private fun getSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId("216718482095-2g9o5kfu9hg5pshvuju3hnlulk49bk50.apps.googleusercontent.com")
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    fun signInWithGoogle(callback: (IntentSenderRequest?) -> Unit) {
        signInClient.beginSignIn(getSignInRequest())
            .addOnSuccessListener { result ->
                callback(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())
            }
            .addOnFailureListener { e ->
                callback(null)
            }
    }


    fun handleGoogleSignInResult(data: Intent, callback: (FirebaseUser?) -> Unit) {
        try {
            val credential = signInClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken
            if (idToken != null) {
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener { task ->
                        callback(if (task.isSuccessful) auth.currentUser else null)
                    }
            } else {
                callback(null)
            }
        } catch (e: ApiException) {
            callback(null)
        }
    }

    fun signOut() {
        auth.signOut()
        signInClient.signOut()
    }
}