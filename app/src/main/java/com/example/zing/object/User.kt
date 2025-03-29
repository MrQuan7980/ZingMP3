package com.example.zing.`object`

data class User(val userId : String, val first : String, val last : String, val email : String, val phone : String, val password : String)

data class Login(val email: String, val password: String)