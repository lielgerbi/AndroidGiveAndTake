package com.example.givetakeapp.util

import android.util.Patterns

fun validateEmail(email: String): RegisterValidation {
    if (email.isEmpty() || email.isBlank() || Patterns.EMAIL_ADDRESS.equals(email))
        return RegisterValidation.Failed("Email is invalid!")

    return RegisterValidation.Success
}

fun validatePassword(password: String): RegisterValidation {
    val minimumLength = 6
    val maximumLength = 16
    if (password.isEmpty() || password.length < minimumLength || password.length > maximumLength)
        return RegisterValidation.Failed("password is invalid!")

    return RegisterValidation.Success
}