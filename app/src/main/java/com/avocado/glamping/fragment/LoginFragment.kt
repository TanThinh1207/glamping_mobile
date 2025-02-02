package com.avocado.glamping.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.avocado.glamping.R
import com.avocado.glamping.activity.UserCampActivity
import com.avocado.glamping.viewmodel.LoginState
import com.avocado.glamping.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.login_fragment) {
    private val loginViewModel by viewModels<LoginViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginBtn : AppCompatButton = view.findViewById(R.id.loginBtn)
        val progressBar : ProgressBar = view.findViewById(R.id.progressBarLogin)
        val registerBtn : TextView = view.findViewById(R.id.dontHaveAccount)

        loginViewModel.loginState.observe(viewLifecycleOwner, Observer { state ->
            when(state){
                is LoginState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    loginBtn.visibility = View.GONE
                }
                is LoginState.Success -> {
                    progressBar.visibility = View.GONE
                    loginBtn.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Login Successful!", Toast.LENGTH_SHORT).show()
                    Intent(requireContext(), UserCampActivity::class.java).also {
                        intent -> intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                }
                is LoginState.Error -> {
                    progressBar.visibility = View.GONE
                    loginBtn.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        registerBtn.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        loginBtn.setOnClickListener{
            val email : String = view.findViewById<EditText>(R.id.edEmailLogin).text.toString().trim()
            val password : String = view.findViewById<EditText>(R.id.edPasswordLogin).text.toString().trim()
            loginViewModel.login(email, password)
        }
    }
}