package com.avocado.glamping.fragment.authentication

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.avocado.glamping.R
import com.avocado.glamping.activity.HostingActivity
import com.avocado.glamping.repository.AuthRepository
import com.avocado.glamping.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


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

        val skipForNow: TextView = view.findViewById(R.id.skipForNow)
        val googleBtn: MaterialButton = view.findViewById(R.id.googleBtn)


        skipForNow.setOnClickListener {
            val intent = Intent(requireContext(), HostingActivity::class.java)
            startActivity(intent)
        }

        googleBtn.setOnClickListener {
            Log.d("AuthDebug", "Google Sign-In button clicked")
            authViewModel.signInWithGoogle()
        }

        authViewModel.signInIntent.observe(viewLifecycleOwner) {
            intent -> intent?.let {
                oneTapSignInLauncher.launch(it)
            }
        }

        authViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                Log.d("AuthViewModel", "Navigating to next fragment")
                val displayName = user.displayName
                val parts = displayName?.split(" ")
                val action = FragmentAuthenticationDirections.actionFragmentAuthenticationToFragmentRegisterInformation(
                    firstname = parts?.getOrNull(0) ?: "",
                    lastname = parts?.getOrNull(1) ?: "",
                    email = user.email ?: "",
                    phone = user.phoneNumber ?: ""
                )
                findNavController().navigate(action)
            } else {
                Log.e("AuthViewModel", "User is null, not navigating")
            }
        }
    }
}