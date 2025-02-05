package com.avocado.glamping.fragment.authentication

import android.content.Intent
import android.os.Bundle
import android.view.View
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
import com.avocado.glamping.activity.LoginRegisterActivity
import com.avocado.glamping.viewmodel.RegisterState
import com.avocado.glamping.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.register_fragment) {

    private val registerViewModel by viewModels<RegisterViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val registerBtn : AppCompatButton = view.findViewById(R.id.registerBtn)
        val progressBar : ProgressBar = view.findViewById(R.id.progressBarRegister)
        val loginBtn : TextView = view.findViewById(R.id.alreadyHaveAccount)


        registerViewModel.registerState.observe(viewLifecycleOwner, Observer { state ->
            when(state){
                is RegisterState.Loading ->{
                    progressBar.visibility = View.VISIBLE
                    registerBtn.visibility = View.GONE
                }

                is RegisterState.Success ->{
                    progressBar.visibility = View.GONE
                    registerBtn.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Register Successful", Toast.LENGTH_SHORT).show()
                    Intent(requireActivity(), LoginRegisterActivity::class.java).also {
                        intent -> intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }

                is RegisterState.Error ->{
                    progressBar.visibility = View.GONE
                    registerBtn.visibility = View.VISIBLE
                    Toast.makeText(requireContext() ,state.mess, Toast.LENGTH_SHORT).show()
                }
            }
        })


        loginBtn.setOnClickListener{
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        registerBtn.setOnClickListener{
            val firstName : String = view.findViewById<EditText>(R.id.edFirstName).text.toString().trim()
            val lastName : String = view.findViewById<EditText>(R.id.edLastName).text.toString().trim()
            val email : String = view.findViewById<EditText>(R.id.edEmailRegister).text.toString().trim()
            val password : String = view.findViewById<EditText>(R.id.edPasswordRegister).text.toString().trim()
            val confirmPassword : String = view.findViewById<EditText>(R.id.edConfirmPasswordRegister).text.toString().trim()

            if (password != confirmPassword) Toast.makeText(requireContext(), "Confirm password does not match", Toast.LENGTH_SHORT).show()
            else registerViewModel.register(firstName,lastName,email,password)
        }
    }


}