package com.example.givetakeapp.data
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val category: String,
    val userEmail: String,
    val city: String,
    var imagePath: String = ""
) : Parcelable {
    constructor(): this("","","","","")
}