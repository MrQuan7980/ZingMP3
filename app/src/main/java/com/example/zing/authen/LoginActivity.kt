package com.example.zing.authen

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.zing.databinding.ActivityLoginBinding
import com.example.zing.`object`.Login
import com.example.zing.viewmodel.LoginViewModel
import progressbar
import setMessage

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var preferenceManager: PreferenceManager

    private val loginViewModel : LoginViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(LoginViewModel::class.java))
                {
                    return LoginViewModel(preferenceManager) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        })[LoginViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(this)
        settingShow()
        eventHandling()
        observeViewModel()
    }
    private fun eventHandling()
    {
        binding.forgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }

        binding.textRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        binding.buttonLogin.setOnClickListener {
            authenticateUser()
        }
    }
    private fun authenticateUser()
    {
        progressbar(
            true,
            binding.progressLogin,
            binding.buttonLogin,
            View.VISIBLE,
            View.INVISIBLE
        )
        val email = binding.inputEmail.text.toString()
        val password = binding.inputPassword.text.toString()

        if (loginViewModel.validateInput(email, password))
        {
            val login = Login(email, password);

            loginViewModel.login(login)
        }
    }
    private fun observeViewModel()
    {
        loginViewModel.errorMessage.observe (this) { message ->
            message?.let { setMessage(it) }
            progressbar(
                false,
                binding.progressLogin,
                binding.buttonLogin,
                View.VISIBLE,
                View.INVISIBLE
            )
        }
        loginViewModel.success.observe (this) { state ->
            if (state == true) {
                setMessage("Login Success")
            }
            progressbar(
                false,
                binding.progressLogin,
                binding.buttonLogin,
                View.VISIBLE,
                View.INVISIBLE
            )
        }
    }
    private fun settingShow()
    {
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }
}