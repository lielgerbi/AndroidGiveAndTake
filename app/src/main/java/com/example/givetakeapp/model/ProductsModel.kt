package com.example.givetakeapp.model

import com.example.givetakeapp.MainApp
import com.example.givetakeapp.data.Product

class ProductsModel private constructor() {
    private val database = MainApp.database
    private var executor = MainApp.executorService

    companion object {
        val instance: ProductsModel = ProductsModel()
    }

    fun getAllProducts(callback: (List<Product>) -> Unit) {
        executor.execute {
            val products = database.productDao().getAllProducts()
            MainApp.mainHandler.post {
                callback(products)
            }
        }
    }

    fun insertProduct(product: Product, callback: () -> Unit) {
        executor.execute {
            MainApp.database.productDao().insertProduct(product)
            MainApp.mainHandler.post {
                callback()
            }
        }
    }

    fun getAllProductsByCategory(category: String, callback: (List<Product>) -> Unit) {
        executor.execute {
            val products = database.productDao().getAllProductsByCategory(category)
            MainApp.mainHandler.post {
                callback(products)
            }
        }
    }

    fun getAllProductsByUser(userEmail: String, callback: (List<Product>) -> Unit) {
        executor.execute {
            val products = database.productDao().getAllProductsByUser(userEmail)
            MainApp.mainHandler.post {
                callback(products)
            }
        }
    }

    fun deleteProduct(id: String, callback: () -> Unit) {
        executor.execute {
            MainApp.database.productDao().deleteProduct(id)
            MainApp.mainHandler.post {
                callback()
            }
        }
    }
}