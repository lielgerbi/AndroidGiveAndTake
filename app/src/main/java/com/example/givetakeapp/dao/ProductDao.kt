package com.example.givetakeapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.givetakeapp.data.Product

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE isDeleted = 0")
    fun getAllProducts(): List<Product>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(product: Product)

    @Query("SELECT * FROM products WHERE category = :category AND isDeleted = 0")
    fun getAllProductsByCategory(category: String): List<Product>

    @Query("SELECT * FROM products WHERE userEmail = :userEmail AND isDeleted = 0")
    fun getAllProductsByUser(userEmail: String): List<Product>

    @Query("UPDATE products SET isDeleted = 1 where id = :id")
    fun deleteProduct(id: String)
}