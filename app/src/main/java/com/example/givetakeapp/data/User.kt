package com.example.givetakeapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = false)
    val email: String,
    val firstName: String,
    val lastName: String,
    var imagePath: String = ""
){
    constructor(): this("","","","")
}
