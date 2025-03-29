package com.example.zing.authen

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.zing.databinding.ActivityForgotPasswordBinding
import com.example.zing.viewmodel.ForgotPasswordModel
import com.example.zing.viewmodel.ForgotPasswordState
import returnOTP
import setMessage
import showView

class ForgotPassword : AppCompatActivity() {
    private lateinit var binding : ActivityForgotPasswordBinding
    private val viewModelForgotPassword : ForgotPasswordModel by viewModels()
    private var email = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeViewModel()
        eventHandling()
        showMessage()
        showNumberViewOTP()
    }
    private fun eventHandling() {
        binding.buttonForgot.setOnClickListener {
            email = binding.inputEmail.text.toString()
            if (viewModelForgotPassword.validateEmail(email)) {

                viewModelForgotPassword.sentOTPByEmail(email)
            }
        }
        binding.createAccount.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        binding.resendOtp.setOnClickListener {
            viewModelForgotPassword.resentOTP(email)
        }

        binding.buttonForgotPassword.setOnClickListener {
            val password = binding.inputPassword.text.toString()
            val confirm_password = binding.inputConfirmPassword.text.toString()

            if (viewModelForgotPassword.validatePassword(password, confirm_password))
            {
                viewModelForgotPassword.changePassword(password)
            }
        }
        binding.backSignIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }
    private fun observeViewModel(){
        viewModelForgotPassword.state.observe(this) {state ->
            when (state) {
                is ForgotPasswordState.EnterOTP -> showViewOTP()
                is ForgotPasswordState.ResetPassword -> showChangePassword()
                is ForgotPasswordState.ChangeSuccess -> showViewChangeSuccess()
            }
        }

        viewModelForgotPassword.progressbar.observe (this) { isLoading ->
            binding.buttonForgot.isEnabled = !isLoading
            binding.buttonForgot.visibility = if (!isLoading) View.VISIBLE else View.GONE
            binding.progressbarForgot.visibility = if (isLoading) View.VISIBLE else View.GONE

            binding.buttonForgotPassword.isEnabled = !isLoading
            binding.progressbarForgotPassword.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
    private fun showChangePassword() {
        showView(binding.viewChangePassword, View.VISIBLE)
        showView(binding.viewOtp, View.GONE)
        showView(binding.viewShowForgotPassword, View.VISIBLE)
        showView(binding.viewEnterOTP, View.GONE)
        showView(binding.notEnterOTP, View.VISIBLE)
        showView(binding.notShowForgotPassword, View.GONE)
    }
    private fun showViewOTP() {
        binding.emailSent.setText(email)
        showView(binding.viewInputEmail, View.GONE)
        showView(binding.viewOtp, View.VISIBLE)
        showView(binding.showEnterEmail, View.GONE)
        showView(binding.notShowEnterEmail, View.VISIBLE)
        showView(binding.viewEnterOTP, View.VISIBLE)
        showView(binding.notEnterOTP, View.INVISIBLE)

        binding.buttonOTP.setOnClickListener {
            val otp = returnOTP(binding.numberOne.text.toString(),binding.numberTwo.text.toString(), binding.numberThree.text.toString(), binding.numberFour.text.toString(), binding.numberFive.text.toString(), binding.numberSix.text.toString() )

            viewModelForgotPassword.verifyOTP(otp)
        }
    }
    private fun showViewChangeSuccess(){
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
    private fun showMessage() {
        viewModelForgotPassword.errorMessage.observe(this) { message ->
            if (!message.isNullOrEmpty()) {
                setMessage(message)
            }
        }
    }
    private fun showNumberViewOTP()
    {
        binding.numberOne.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                if (charSequence?.length == 1)
                {
                    binding.numberTwo.requestFocus()
                    binding.imageOvalOne.visibility = View.VISIBLE
                }
            }
            override fun afterTextChanged(editable: Editable?) {}
        })
        binding.numberTwo.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                if (charSequence?.length == 1)
                {
                    binding.numberThree.requestFocus()
                    binding.imageOvalTwo.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
        binding.numberThree.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                if (charSequence?.length == 1)
                {
                    binding.numberFour.requestFocus()
                    binding.imageOvalThree.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
        binding.numberFour.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                if (charSequence?.length == 1)
                {
                    binding.numberFive.requestFocus()
                    binding.imageOvalFour.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
        binding.numberFive.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                if (charSequence?.length == 1)
                {
                    binding.numberSix.requestFocus()
                    binding.imageOvalFive.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
        binding.numberSix.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                if (charSequence?.length == 1)
                {
                    binding.imageOvalSix.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })

    }

}

