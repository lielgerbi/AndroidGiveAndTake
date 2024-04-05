package com.example.givetakeapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.givetakeapp.data.Product

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun getCategories()

    @Query("SELECT * FROM products")
    suspend fun getAllUsers(): List<Product>
}