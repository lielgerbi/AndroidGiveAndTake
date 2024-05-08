package com.example.givetakeapp.model

import com.example.givetakeapp.MainApp
import com.example.givetakeapp.data.Product

class ProductsModel private constructor() {
    private val database = MainApp.database
    private var executor = MainApp.executorService
    private val firestoreModel = ProductsFirestoreModel()

    companion object {
        val instance: ProductsModel = ProductsModel()
    }

    fun getAllProducts(callback: (List<Product>) -> Unit) {
        val lastUpdated: Long = Product.lastUpdated

        firestoreModel.getAllProducts(lastUpdated) { list ->
            executor.execute {
                var time = lastUpdated
                for (product in list) {
                    database.productDao().insertProduct(product)

                    product.lastUpdated?.let {
                        if (time < it) {
                            time = product.lastUpdated ?: System.currentTimeMillis()
                        }
                    }
                }
                Product.lastUpdated = time
                val products = database.productDao().getAllProducts()
                MainApp.mainHandler.post {
                    callback(products)
                }
            }
        }
    }

    fun insertProduct(product: Product, callback: () -> Unit) {
        firestoreModel.insertProduct(product, callback)

//        executor.execute {
//            MainApp.database.productDao().insertProduct(product)
//            MainApp.mainHandler.post {
//                callback()
//            }
//        }
    }

    fun getAllProductsByCategory(category: String, callback: (List<Product>) -> Unit) {
        firestoreModel.getAllProductsByCategory(category, callback)

//        executor.execute {
//            val products = database.productDao().getAllProductsByCategory(category)
//            MainApp.mainHandler.post {
//                callback(products)
//            }
//        }
    }

    fun getAllProductsByUser(userEmail: String, callback: (List<Product>) -> Unit) {
        firestoreModel.getAllProductsByUser(userEmail, callback)

//        executor.execute {
//            val products = database.productDao().getAllProductsByUser(userEmail)
//            MainApp.mainHandler.post {
//                callback(products)
//            }
//        }
    }

    fun deleteProduct(id: String, callback: () -> Unit) {
        firestoreModel.deleteProduct(id, callback)

//        executor.execute {
//            MainApp.database.productDao().deleteProduct(id)
//            MainApp.mainHandler.post {
//                callback()
//            }
//        }
    }
}