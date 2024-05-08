package com.example.givetakeapp.model

import com.example.givetakeapp.MainApp
import com.example.givetakeapp.data.User

class UserModel private constructor() {
    private val database = MainApp.database
    private var executor = MainApp.executorService

    companion object {
        val instance: UserModel = UserModel()
    }

    fun insertUser(user: User, callback: () -> Unit) {
        executor.execute {
            MainApp.database.userDao().insertUser(user)
            MainApp.mainHandler.post {
                callback()
            }
        }
    }

    fun getUserByEmail(email: String, callback: (User) -> Unit) {
        executor.execute {
            val user = database.userDao().getUserByEmail(email)
            MainApp.mainHandler.post {
                callback(user)
            }
        }
    }
}