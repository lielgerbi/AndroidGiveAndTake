package com.example.givetakeapp.helper
import android.content.Context
import android.content.SharedPreferences
import com.example.givetakeapp.SharedData

class UserManager(context: Context)  {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_pref", Context.MODE_PRIVATE)

    // Function to save user authentication status
    fun saveUserLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean("isLoggedIn", isLoggedIn).apply()
        if (isLoggedIn) {
                sharedPreferences.edit().putString("userEmail", SharedData.myVariable).apply()
        }
    }

    // Function to check if the user is logged in
    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }


    // Function to get saved user email
    fun getUserEmail(): String? {
        return sharedPreferences.getString("userEmail", null)
    }
}
