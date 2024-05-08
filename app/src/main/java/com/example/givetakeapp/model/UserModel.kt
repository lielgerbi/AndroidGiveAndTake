package com.example.givetakeapp.model

import com.example.givetakeapp.MainApp
import com.example.givetakeapp.data.User
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserModel private constructor() {
    private val database = MainApp.database
    private var executor = MainApp.executorService
    private val remoteDB = Firebase.firestore

    companion object {
        val instance: UserModel = UserModel()
    }

    fun insertUser(user: User, callback: () -> Unit) {
        val userMap = toUserMap(user)
        remoteDB.collection("users")
            .document(user.email)
            .set(userMap)
            .addOnSuccessListener {
                callback()
            }

//        executor.execute {
//            MainApp.database.userDao().insertUser(user)
//            MainApp.mainHandler.post {
//                callback()
//            }
//        }
    }

    fun getUserByEmail(email: String, callback: (User) -> Unit) {
        remoteDB.collection("users")
            .whereEqualTo("email", email)
            .limit(1)
            .get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val user: User = fromUserMap(it.result.first())
                        callback(user)
                    }
                    false -> callback(User())
                }
            }

//        executor.execute {
//            val user = database.userDao().getUserByEmail(email)
//            MainApp.mainHandler.post {
//                callback(user)
//            }
//        }
    }

    private fun toUserMap(user: User): HashMap<String, String> {
        return hashMapOf(
            "email" to user.email,
            "firstName" to user.firstName,
            "lastName" to user.lastName,
            "imagePath" to user.imagePath
        )
    }

    private fun fromUserMap(json: QueryDocumentSnapshot): User {
        val email = json.getString("email") ?: ""
        val firstName = json.getString("firstName") ?: ""
        val lastName = json.getString("lastName") ?: ""
        val imagePath = json.getString("imagePath") ?: ""

        return User(email, firstName, lastName, imagePath)
    }
}