package com.avocado.glamping.activity

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Observer
import com.avocado.glamping.R
import com.avocado.glamping.viewmodel.RegisterState
import com.avocado.glamping.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private val registerViewModel : RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        val registerBtn : AppCompatButton = findViewById(R.id.registerBtn)
        val progressBar : ProgressBar = findViewById(R.id.progressBarRegister)

        registerViewModel.registerState.observe(this, Observer { state ->
            when(state){
                is RegisterState.Loading ->{
                    progressBar.visibility = View.VISIBLE
                    registerBtn.visibility = View.GONE
                }

                is RegisterState.Success ->{
                    progressBar.visibility = View.GONE
                    registerBtn.visibility = View.VISIBLE
                    Toast.makeText(this, "Register Successful", Toast.LENGTH_SHORT).show()
                }

                is RegisterState.Error ->{
                    progressBar.visibility = View.GONE
                    registerBtn.visibility = View.VISIBLE
                    Toast.makeText(this, state.mess, Toast.LENGTH_SHORT).show()
                }
            }
        })

        registerBtn.setOnClickListener{
            val firstName : String = findViewById<EditText>(R.id.edFirstName).text.toString().trim()
            val lastName : String = findViewById<EditText>(R.id.edLastName).text.toString().trim()
            val email : String = findViewById<EditText>(R.id.edEmailRegister).text.toString().trim()
            val password : String = findViewById<EditText>(R.id.edPasswordRegister).text.toString().trim()
            val confirmPassword : String = findViewById<EditText>(R.id.edConfirmPasswordRegister).text.toString().trim()

            if (password != confirmPassword) Toast.makeText(this, "Confirm password does not match", Toast.LENGTH_SHORT).show()
            else registerViewModel.register(firstName,lastName,email,password)
        }
    }
}