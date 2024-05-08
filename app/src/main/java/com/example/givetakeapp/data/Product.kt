package com.example.givetakeapp.data
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.givetakeapp.MainApp
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val category: String,
    val userEmail: String,
    val city: String,
    val description: String,
    var imagePath: String = "",
    var lastUpdated: Long? = null
) : Parcelable {
    constructor(): this("","","","","","")

    companion object {
        var lastUpdated: Long = 0
    }
}