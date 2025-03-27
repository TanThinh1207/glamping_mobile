package com.avocado.glamping.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.fragment.app.Fragment
import com.avocado.glamping.BuildConfig
import com.avocado.glamping.data.model.dto.User
import com.avocado.glamping.data.model.req.AuthRequest
import com.avocado.glamping.data.model.resp.AuthResponse
import com.avocado.glamping.data.model.resp.BaseResponse
import com.avocado.glamping.data.model.resp.UserResponse
import com.avocado.glamping.service.AuthApiService
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
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

class AuthRepository @Inject constructor(
    @ApplicationContext private val context : Context,
    private val apiService: AuthApiService
) {
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    private val signInClient : SignInClient = Identity.getSignInClient(context)

    private fun getSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(BuildConfig.SERVER_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(false) // Show all accounts
                    .build()
            )
            .setAutoSelectEnabled(false) // Ensure new account picker shows up
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


    suspend fun handleGoogleSignInResult(data: Intent): Result<BaseResponse<AuthResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val credential = signInClient.getSignInCredentialFromIntent(data)
                val idToken = credential.googleIdToken ?: return@withContext Result.failure(Exception("ID Token is null"))

                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()
                val firebaseUser = authResult.user

                val fcmToken = FirebaseMessaging.getInstance().token.await()
                val deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                val response: Response<BaseResponse<AuthResponse>> = apiService.loginWithFireBase(AuthRequest(firebaseUser?.getIdToken(true)?.await()?.token ?: "", fcmToken, deviceId))
                if (response.isSuccessful) {
                    response.body()?.let {
                        Result.success(it)
                    } ?: Result.failure(Exception("Response body is null"))
                } else {
                    Log.e("AuthRepository", "API verification failed: ${response.errorBody()?.string()}")
                    Result.failure(Exception("Failed to authenticate"))
                }
            } catch (e: ApiException) {
                Log.e("AuthRepository", "Sign-in failed: ${e.message}")
                Result.failure(Exception("Failed to authenticate"))
            } catch (e: Exception) {
                Log.e("AuthRepository", "Unexpected error: ${e.message}")
                Result.failure(Exception("Failed to authenticate"))
            }
        }
    }

    fun signOut() {
        auth.signOut()
        signInClient.signOut()
    }

}