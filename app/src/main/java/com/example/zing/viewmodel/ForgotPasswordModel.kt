package com.example.zing.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zing.authen.Mail
import com.example.zing.authen.PreferenceManager
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.Executors

class ForgotPasswordModel : ViewModel() {
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage : LiveData<String?> get() = _errorMessage

    private val _otp = MutableLiveData<String?>()
    val otp: LiveData<String?> get() = _otp

    private val _state = MutableLiveData<ForgotPasswordState>()
    val state: LiveData<ForgotPasswordState> get() = _state

    private val _userId = MutableLiveData<String?>()
    val userId : LiveData<String?> get() = _userId

    private val _progressbar = MutableLiveData<Boolean>(false)
    val progressbar : LiveData<Boolean> get() = _progressbar

    private val executor = Executors.newSingleThreadExecutor()

    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()

    fun validateEmail(email : String) : Boolean
    {
        return when{
            email.isEmpty() -> {
                _errorMessage.value = "Please enter your email"
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _errorMessage.value = "Invalid email, please enter correct format"
                false
            }
            else -> true
        }
    }
    fun sentOTPByEmail(email : String) {
        _errorMessage.postValue(null)
        _progressbar.postValue(true)
        checkEmailExist(PreferenceManager.KEY_EMAIL, email) {exist ->
            _progressbar.postValue(false)
            when{
                exist -> sentOTP(email)
                else -> _errorMessage.value = "Email not exist"
            }
        }
    }
    private fun sentOTP(email: String) {
        val newOTP = randomOTP()
        _otp.value = newOTP
        val senderEmail = "appzingmp3@gmail.com"
        val senderPassword = "kcyl ogdk csup lhoi"

        val mail = Mail(senderEmail, senderPassword)
        mail.set_to(arrayOf(email))
        mail.set_subject("🔐 Mã xác minh tài khoản Zing MP3 của bạn")

        val emailBody = """
              🎵 Xin chào,  
            
              Bạn vừa yêu cầu đặt lại mật khẩu cho tài khoản Zing MP3.  
              Đây là mã xác minh của bạn:  
            
              ${newOTP}  
            
              Hãy nhập mã này vào ứng dụng để tiếp tục quá trình đặt lại mật khẩu.  
              Nếu bạn không yêu cầu thao tác này, vui lòng bỏ qua email này.  
            
              Chúc bạn có trải nghiệm tuyệt vời cùng Zing MP3! 🎶  
            
              Trân trọng,  
              Đội ngũ Zing MP3    
                """
            .trimIndent()

        mail.setBody(emailBody)

        executor.execute {
            val success = mail.send()
            if (success) {
                _state.postValue(ForgotPasswordState.EnterOTP)
            } else {
                _errorMessage.postValue("Email could not be sent. Please try again!")
            }
        }
    }
    fun resentOTP(email: String)
    {
        sentOTP(email)
        _errorMessage.value = "OTP has been resent"
    }
    fun verifyOTP(userOTP : String)
    {
        if (userOTP == _otp.value)
        {
            _state.value = ForgotPasswordState.ResetPassword
        }
        else
        {
            _errorMessage.value = "Code OTP fail"
        }
    }
    private fun randomOTP() : String{
        return (100000..999999).random().toString()
    }
    private fun checkEmailExist(key : String, email : String, onResult: (Boolean) -> Unit)
    {
        db.collection(PreferenceManager.KEY_TABLE_USER)
            .whereEqualTo(key, email)
            .get()
            .addOnSuccessListener { exist ->
                if (!exist.isEmpty)
                {
                    val document = exist.documents[0]
                    _userId.postValue(document.id)
                    onResult(true)
                }
                else
                {
                    onResult(false)
                }
            }
            .addOnFailureListener { error ->
                _errorMessage.postValue("${error}")
            }
    }
    fun validatePassword(password : String, confirm : String) : Boolean
    {
        return when{
            password.isEmpty() -> {
                _errorMessage.value = "Please enter your password"
                false
            }
            confirm.isEmpty() -> {
                _errorMessage.value = "Please enter your confirm password"
                false
            }
            password.length < 6 -> {
                _errorMessage.value = "Password must be at least 6 characters"
                false
            }
            password != (confirm) -> {
                _errorMessage.value = "Password does not match"
                false
            }
            else -> true
        }
    }
    fun changePassword(password : String){
        val id = _userId.value
        if (id.isNullOrEmpty())
        {
            _errorMessage.value = "User ID not found. Please try again."
            return
        }
        _progressbar.postValue(true)
        db.collection(PreferenceManager.KEY_TABLE_USER)
            .document(id)
            .update(PreferenceManager.KEY_PASSWORD, password)
            .addOnSuccessListener {
                _progressbar.postValue(false)
                _errorMessage.value = "Change Password Success"
                _state.value = ForgotPasswordState.ChangeSuccess
            }
            .addOnFailureListener { error ->
                _progressbar.postValue(false)
                _errorMessage.value = "Error changing password ${error.message}"
            }
    }
}

sealed class ForgotPasswordState{
    object ChangeSuccess: ForgotPasswordState()
    object EnterOTP : ForgotPasswordState()
    object ResetPassword : ForgotPasswordState()

}