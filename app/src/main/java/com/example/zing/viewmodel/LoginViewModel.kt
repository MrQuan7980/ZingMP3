package com.example.zing.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zing.authen.PreferenceManager
import com.example.zing.`object`.Login
import com.google.firebase.firestore.FirebaseFirestore

class LoginViewModel (private val preferenceManager: PreferenceManager) : ViewModel() {

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage : LiveData<String?> get() = _errorMessage

    private val _stateSuccess = MutableLiveData<Boolean>()
    val success : LiveData<Boolean> get() = _stateSuccess

    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()

    fun validateInput(email : String, password : String) : Boolean
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
            password.isEmpty() -> {
                _errorMessage.value = "Please enter your password"
                false
            }
            else -> true
        }
    }

    fun login(login: Login)
    {
        db.collection(PreferenceManager.KEY_TABLE_USER)
            .whereEqualTo(PreferenceManager.KEY_EMAIL, login.email)
            .whereEqualTo(PreferenceManager.KEY_PASSWORD, login.password)
            .get()
            .addOnSuccessListener { state ->
                if (!state.isEmpty)
                {
                    val document = state.documents[0]
                    preferenceManager.putBoolean(PreferenceManager.KEY_IS_SIGN_IN, true)

                    preferenceManager.getString(PreferenceManager.KEY_USER_ID)
                    preferenceManager.getString(PreferenceManager.KEY_FIRST_NAME)
                    preferenceManager.getString(PreferenceManager.KEY_LAST_NAME)
                    preferenceManager.getString(PreferenceManager.KEY_EMAIL)
                    preferenceManager.getString(PreferenceManager.KEY_PHONE)

                    _stateSuccess.postValue(true)
                }
                else
                {
                    _errorMessage.postValue("Incorrect email or password")
                    _stateSuccess.postValue(false)
                }
            }
            .addOnFailureListener { error ->
                _errorMessage.postValue("Login failed: ${error.message}")
            }
    }
}