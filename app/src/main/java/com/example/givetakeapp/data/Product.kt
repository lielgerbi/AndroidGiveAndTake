package com.example.givetakeapp.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val category: String,
    val userEmail: String,
    val city: String,
    var imagePath: String = ""
){
    constructor(): this("","","","","")
}