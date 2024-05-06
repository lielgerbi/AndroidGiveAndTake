package com.example.givetakeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.givetakeapp.MainApp
import com.example.givetakeapp.SharedData
import com.example.givetakeapp.data.Product
import com.example.givetakeapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MyProductsViewModel @Inject constructor() : ViewModel() {
    private val _myProducts =
        MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val myProducts = _myProducts.asStateFlow()

    init {
        getCartProducts()
    }

    private fun getCartProducts() {
        var allProducts: List<Product>;
        viewModelScope.launch {
            _myProducts.emit(Resource.Loading())
            runBlocking {
                // Retrieve all products from the database
                allProducts =
                    MainApp.database.productDao().getAllProductsByUser(SharedData.myVariable)
            }
            viewModelScope.launch {
                _myProducts.emit(Resource.Success(allProducts))
            }
        }
    }

    fun deleteProduct(productToDelete: Product) {
        runBlocking {
            MainApp.database.productDao().deleteProduct(productToDelete.id)
        }
        val currentProducts = _myProducts.value.data ?: return // Exit if data is null
        val updatedProducts = currentProducts.toMutableList().apply {
            remove(productToDelete)
        }

        // Update the _cartProducts MutableStateFlow with the new list of products
        _myProducts.value = Resource.Success(updatedProducts)
    }
}