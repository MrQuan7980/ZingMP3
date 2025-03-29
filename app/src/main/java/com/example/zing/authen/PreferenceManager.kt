package com.example.zing.authen

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferenceManager (context: Context) {
    private val sharedPreferences : SharedPreferences = context.getSharedPreferences(
        PreferenceManager.KEY_NAME,
        Context.MODE_PRIVATE)

    fun putBoolean(key : String, value : Boolean)
    {
        sharedPreferences.edit() { putBoolean(key, value) }
    }

    fun getBoolean(key : String) : Boolean

    {
        return sharedPreferences.getBoolean(key, false)
    }

    fun putString(key : String, value: String)
    {
        sharedPreferences.edit() {putString(key, value)}
    }

    fun getString(key: String) : String?
    {
        return sharedPreferences.getString(key, null)
    }

    fun clear()
    {
        sharedPreferences.edit() {clear().apply()}
    }

    companion object{
        const val KEY_NAME = "Zing MP3"
        const val KEY_IS_SIGN_IN = "is_sign_in"
        const val KEY_TABLE_USER = "users"

        const val KEY_USER_ID = "user_id"
        const val KEY_FIRST_NAME = "first"
        const val KEY_LAST_NAME = "last"
        const val KEY_EMAIL = "email"
        const val KEY_PHONE = "phone"
        const val KEY_PASSWORD = "password"
    }
}