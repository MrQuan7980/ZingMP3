package com.example.zing.authen

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.zing.databinding.ActivityRegisterBinding
import com.example.zing.`object`.User
import com.example.zing.viewmodel.RegisterViewModel
import progressbar
import setMessage
import com.example.zing.R
import java.util.UUID

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding
    private lateinit var preferenceManager: PreferenceManager
    private val ID  = UUID.randomUUID().toString()

    private val registerViewModel: RegisterViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
                    return RegisterViewModel(preferenceManager) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        })[RegisterViewModel::class.java]
    }
    private lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = PreferenceManager(this)

        settingShow()
        eventHandling()
        observeViewModel()
    }
    private fun eventHandling()
    {
        binding.buttonRegister.setOnClickListener {
            progressbar(true, binding.progressbarRegister, binding.buttonRegister, View.VISIBLE, View.INVISIBLE)

            val first = binding.inputFirstName.text.toString().trim()
            val last = binding.inputLastName.text.toString().trim()
            val email = binding.inputEmail.text.toString().trim()
            val phone = binding.inputPhone.text.toString().trim()
            val password = binding.inputPassword.text.toString().trim()
            val checkBox : Boolean = binding.checkBox.isChecked

            if (registerViewModel.validateInput(checkBox,first, last, email, phone, password))
            {
                val user = User(ID, first, last, email, phone, password)

                registerViewModel.saveRegister(user)
            }
        }
    }
    private fun observeViewModel() {
        registerViewModel.errorMessage.observe(this) { message ->
            message?.let { setMessage(it) }
            progressbar(false, binding.progressbarRegister, binding.buttonRegister, View.VISIBLE, View.INVISIBLE)
        }
        registerViewModel.stateSuccess.observe(this) { success ->
            if (success == true) {
                setMessage("Register Success!")
                finish()
            }
            progressbar(
                false, binding.progressbarRegister, binding.buttonRegister, View.VISIBLE, View.INVISIBLE)
        }
    }

    private fun settingShow()
    {
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }
}