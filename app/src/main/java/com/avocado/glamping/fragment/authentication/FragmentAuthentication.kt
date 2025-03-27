package com.avocado.glamping.fragment.authentication

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.avocado.glamping.R
import com.avocado.glamping.UserPreferences
import com.avocado.glamping.activity.HostingActivity
import com.avocado.glamping.viewmodel.AuthViewModel
import com.avocado.glamping.viewmodel.AuthenticateState
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FragmentAuthentication : Fragment(R.layout.fragment_authentication) {

    private val authViewModel: AuthViewModel by viewModels()

    private val oneTapSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            Log.d("AuthDebug", "Sign-in activity finished, resultCode: ${result.resultCode}")

            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    result.data?.let {
                        authViewModel.handleSignInResult(it)
                    }
                } catch (e: ApiException) {
                    Log.e("AuthDebug", "Sign-in failed with exception: ${e.statusCode} - ${e.message}", e)
                }
            } else {
                Log.e("AuthDebug", "Sign-in canceled or failed, resultCode: ${result.resultCode}")
            }
        }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val googleBtn: MaterialButton = view.findViewById(R.id.googleBtn)
        val progressBar : ProgressBar = view.findViewById(R.id.progressBar)


        googleBtn.setOnClickListener {
            Log.d("AuthDebug", "Google Sign-In button clicked")
            authViewModel.signInWithGoogle()
        }

        authViewModel.signInIntent.observe(viewLifecycleOwner) {
            intent -> intent?.let {
                oneTapSignInLauncher.launch(it)
            }
        }

//        authViewModel.user.observe(viewLifecycleOwner) { user ->
//            if (user != null) {
//                Log.d("AuthViewModel", "Navigating to next fragment")
//                val displayName = user.displayName
//                val parts = displayName?.split(" ")
//                val action = FragmentAuthenticationDirections.actionFragmentAuthenticationToFragmentRegisterInformation(
//                    firstname = parts?.getOrNull(0) ?: "",
//                    lastname = parts?.getOrNull(1) ?: "",
//                    email = user.email ?: "",
//                    phone = user.phoneNumber ?: ""
//                )
//                findNavController().navigate(action)
//            } else {
//                Log.e("AuthViewModel", "User is null, not navigating")
//            }
//        }


        authViewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthenticateState.Loading -> {
                    // Show progress bar or loading animation
                    googleBtn.visibility = View.INVISIBLE
                    progressBar.visibility = View.VISIBLE
                }
                is AuthenticateState.Success -> {
                    progressBar.visibility = View.GONE
                    val response = state.response

                    Log.d("AuthViewModel", "Login successful: ${response}")

                    if (response.new) {
                        UserPreferences.saveUser(requireContext(), response)
                        findNavController().navigate(
                            FragmentAuthenticationDirections.actionFragmentAuthenticationToFragmentRegisterInformation(
                                email = response.user.email,
                                id = response.user.id.toString()
                            )
                        )
                    } else {
                        UserPreferences.saveUser(requireContext(), response)
                        val intent = Intent(requireContext(), HostingActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
                is AuthenticateState.Error -> {
                    progressBar.visibility = View.GONE
                    googleBtn.visibility = View.VISIBLE
                    Log.e("AuthViewModel", "Login failed: ${state.mess}")
                    Toast.makeText(requireContext(), "Authentication Failed: ${state.mess}", Toast.LENGTH_SHORT).show()
                }
            }
        }


        requestNotificationPermission()


    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM", "Device Token: $token")
        }
    }
}