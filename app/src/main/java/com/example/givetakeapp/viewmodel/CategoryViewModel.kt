package com.example.givetakeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.givetakeapp.MainApp
import com.example.givetakeapp.data.Category
import com.example.givetakeapp.data.Product
import com.example.givetakeapp.model.ProductsModel
import com.example.givetakeapp.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CategoryViewModel(
    private val category: Category
) : ViewModel() {

    private val _allProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val allProducts: StateFlow<Resource<List<Product>>> = _allProducts

    init {
        fetchAllProducts()
    }

    private fun fetchAllProducts() {
        var allProducts: List<Product>;
        viewModelScope.launch {
            _allProducts.emit(Resource.Loading())
            ProductsModel.instance.getAllProductsByCategory(category.category) { products ->
                allProducts = products

                viewModelScope.launch {
                    _allProducts.emit(Resource.Success(allProducts))
                }
            }
        }
    }
}