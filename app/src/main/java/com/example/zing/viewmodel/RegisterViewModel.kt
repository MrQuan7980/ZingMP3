package com.example.zing.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.zing.authen.PreferenceManager
import com.example.zing.`object`.User
import com.google.android.gms.common.api.Result
import com.google.firebase.firestore.FirebaseFirestore

class RegisterViewModel (private val preferenceManager: PreferenceManager) : ViewModel() {
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _stateSuccess = MutableLiveData<Boolean?>()
    val stateSuccess: LiveData<Boolean?> get() = _stateSuccess

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun validateInput(
        isCheckBox: Boolean,
        first: String,
        last: String,
        email: String,
        phone: String,
        password: String
    ): Boolean {
        return when {
            !isCheckBox -> {
                _errorMessage.value = "Please select the checkbox"
                false
            }

            first.isEmpty() -> {
                _errorMessage.value = "Please enter your first"
                false
            }

            last.isEmpty() -> {
                _errorMessage.value = "Please enter your name"
                false
            }

            email.isEmpty() -> {
                _errorMessage.value = "Please enter your email"
                false
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _errorMessage.value = "Invalid email, please enter correct format"
                false
            }

            phone.isEmpty() -> {
                _errorMessage.value = "Please enter your phone"
                false
            }

            phone.length != 10 -> {
                _errorMessage.value = "Phone number must be ten digits"
                false
            }

            password.isEmpty() -> {
                _errorMessage.value = "Please enter your password"
                false
            }

            password.length < 6 -> {
                _errorMessage.value = "Password must be at least six characters"
                false
            }

            else -> true
        }
    }
    fun saveRegister(user: User) {
        checkForExistence (PreferenceManager.KEY_EMAIL, user.email) { emailExist ->
        checkForExistence (PreferenceManager.KEY_PHONE, user.phone) { phoneExist ->
                when{
                    emailExist -> _errorMessage.value = "Email already exists"
                    phoneExist -> _errorMessage.value = "Phone number already exists"
                    else -> registerUser(user)
                }
            }
        }
    }
    private fun registerUser(user: User)
    {
        val userRegister = hashMapOf(
            PreferenceManager.KEY_USER_ID to user.userId,
            PreferenceManager.KEY_FIRST_NAME to user.first,
            PreferenceManager.KEY_LAST_NAME to user.last,
            PreferenceManager.KEY_EMAIL to user.email,
            PreferenceManager.KEY_PHONE to user.phone,
            PreferenceManager.KEY_PASSWORD to user.password
        )

        db.collection(PreferenceManager.KEY_TABLE_USER)
            .add(userRegister)
            .addOnSuccessListener { documentReference ->

                preferenceManager.putBoolean(PreferenceManager.KEY_IS_SIGN_IN, true)

                preferenceManager.putString(PreferenceManager.KEY_USER_ID, user.userId)
                preferenceManager.putString(PreferenceManager.KEY_FIRST_NAME, user.first)
                preferenceManager.putString(PreferenceManager.KEY_LAST_NAME, user.last)
                preferenceManager.putString(PreferenceManager.KEY_EMAIL, user.email)
                preferenceManager.putString(PreferenceManager.KEY_PHONE, user.phone)

                _stateSuccess.postValue(true)
            }
            .addOnFailureListener { exception ->
                _errorMessage.postValue("Registration failed: ${exception.message}")
            }
    }
    private fun checkForExistence(key : String ,value : String, onResult: (Boolean) -> Unit){
        db.collection(PreferenceManager.KEY_TABLE_USER)
            .whereEqualTo(key, value)
            .get()
            .addOnSuccessListener { exist ->
                if (!exist.isEmpty)
                {
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
}