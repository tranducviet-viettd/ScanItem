package com.example.scanner.util

fun isEmailValid(email: CharSequence): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isTextValid(minLength: Int,text : String?): Boolean{
    return !(text.isNullOrBlank() || text.length < minLength)
}