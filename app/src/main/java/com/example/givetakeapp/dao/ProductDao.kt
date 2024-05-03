package com.example.givetakeapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.givetakeapp.data.Product

@Dao
interface ProductDao {

    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<Product>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()


    @Query("SELECT * FROM products WHERE category = :category")
    suspend fun getAllProductsByCategory(category: String): List<Product>

    @Query("SELECT * FROM products WHERE userEmail = :userEmail")
    suspend fun getAllProductsByUser(userEmail: String): List<Product>




}