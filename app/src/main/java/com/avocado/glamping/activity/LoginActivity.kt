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
import androidx.lifecycle.ViewModelProvider
import com.avocado.glamping.R
import com.avocado.glamping.viewmodel.LoginState
import com.avocado.glamping.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)



        val loginBtn : AppCompatButton = findViewById(R.id.loginBtn)
        val progressBar : ProgressBar = findViewById(R.id.progressBarLogin)


        loginViewModel.loginState.observe(this, Observer { state ->
            when(state){
                is LoginState.Loading -> {
                    progressBar.visibility = View.VISIBLE
                    loginBtn.visibility = View.GONE
                }
                is LoginState.Success -> {
                    progressBar.visibility = View.GONE
                    loginBtn.visibility = View.VISIBLE
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                }
                is LoginState.Error -> {
                    progressBar.visibility = View.GONE
                    loginBtn.visibility = View.VISIBLE
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        loginBtn.setOnClickListener{
            val email : String = findViewById<EditText>(R.id.edEmailLogin).text.toString().trim()
            val password : String = findViewById<EditText>(R.id.edPasswordLogin).text.toString().trim()
            loginViewModel.login(email, password)
        }

    }
}