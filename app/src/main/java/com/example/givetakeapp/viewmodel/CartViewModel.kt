package com.example.givetakeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.givetakeapp.MainApp
import com.example.givetakeapp.SharedData
import com.example.givetakeapp.data.Product
import com.example.givetakeapp.util.Resource
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(

) : ViewModel() {

    private val _cartProducts =
        MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val cartProducts = _cartProducts.asStateFlow()



    init {
        getCartProducts()
    }


    private fun getCartProducts() {
        var allProducts: List<Product>;
        viewModelScope.launch {
            _cartProducts.emit(Resource.Loading())
            runBlocking {
                // Retrieve all products from the database
                allProducts = MainApp.database.productDao().getAllProductsByUser(SharedData.myVariable)
            }
            viewModelScope.launch {
                _cartProducts.emit(Resource.Success(allProducts))
            }

        }

    }
    fun deleteProduct(productToDelete: Product){

        runBlocking {
            MainApp.database.productDao().deleteProduct(productToDelete.id)
        }
        val currentProducts = _cartProducts.value.data ?: return // Exit if data is null
        val updatedProducts = currentProducts.toMutableList().apply {
            remove(productToDelete)
        }

        // Update the _cartProducts MutableStateFlow with the new list of products
        _cartProducts.value = Resource.Success(updatedProducts)
    }





}