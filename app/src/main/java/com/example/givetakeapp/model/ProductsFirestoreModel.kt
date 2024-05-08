package com.example.givetakeapp.model

import com.example.givetakeapp.data.Product
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProductsFirestoreModel {
    private val remoteDB = Firebase.firestore

    fun getAllProducts(since: Long, callback: (List<Product>) -> Unit) {
        remoteDB.collection("products")
            .whereGreaterThanOrEqualTo("lastUpdated", Timestamp(since, 0))
            .whereEqualTo("isDeleted", false)
            .get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val productsList: MutableList<Product> = mutableListOf()
                        for (json in it.result) {
                            productsList.add(fromProductMap(json))
                        }
                        callback(productsList)
                    }

                    false -> callback(listOf())
                }
            }
    }

    fun insertProduct(product: Product, callback: () -> Unit) {
        val productMap = toProductMap(product)
        remoteDB.collection("products")
            .document(product.id)
            .set(productMap)
            .addOnSuccessListener {
                callback()
            }
    }

    fun getAllProductsByCategory(since: Long, category: String, callback: (List<Product>) -> Unit) {
        remoteDB.collection("products")
            .whereGreaterThanOrEqualTo("lastUpdated", Timestamp(since, 0))
            .whereEqualTo("category", category)
            .whereEqualTo("isDeleted", false)
            .get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val productsList: MutableList<Product> = mutableListOf()
                        for (json in it.result) {
                            productsList.add(fromProductMap(json))
                        }
                        callback(productsList)
                    }

                    false -> callback(listOf())
                }
            }
    }

    fun getAllProductsByUser(since: Long, userEmail: String, callback: (List<Product>) -> Unit) {
        remoteDB.collection("products")
            .whereGreaterThanOrEqualTo("lastUpdated", Timestamp(since, 0))
            .whereEqualTo("userEmail", userEmail)
            .whereEqualTo("isDeleted", false)
            .get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val productsList: MutableList<Product> = mutableListOf()
                        for (json in it.result) {
                            productsList.add(fromProductMap(json))
                        }
                        callback(productsList)
                    }

                    false -> callback(listOf())
                }
            }
    }

    fun deleteProduct(id: String, callback: () -> Unit) {
        remoteDB.collection("products")
            .document(id)
            .update("isDeleted", true)
            .addOnSuccessListener {
            callback()
        }
    }

    private fun toProductMap(product: Product): HashMap<String, Any> {
        return hashMapOf(
            "id" to product.id,
            "category" to product.category,
            "userEmail" to product.userEmail,
            "city" to product.city,
            "description" to product.description,
            "imagePath" to product.imagePath,
            "lastUpdated" to FieldValue.serverTimestamp()
        )
    }

    private fun fromProductMap(json: QueryDocumentSnapshot): Product {
        val id = json.getString("id") ?: ""
        val category = json.getString("category") ?: ""
        val userEmail = json.getString("userEmail") ?: ""
        val city = json.getString("city") ?: ""
        val description = json.getString("description") ?: ""
        val imagePath = json.getString("imagePath") ?: ""
        val product = Product(id, category, userEmail, city, description, imagePath)
        val timestamp: Timestamp? = json.getTimestamp("lastUpdated")
        timestamp?.let {
            product.lastUpdated = it.seconds
        }

        return product
    }
}