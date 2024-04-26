package com.example.givetakeapp.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.givetakeapp.MainApp
import com.example.givetakeapp.data.Category
import com.example.givetakeapp.data.Product
import com.example.givetakeapp.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CategoryViewModel constructor(
    private val category: Category
) : ViewModel() {

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts: StateFlow<Resource<List<Product>>> = _bestProducts

    init {
        fetchBestProducts()
    }

    fun fetchBestProducts() {
        var allProducts: List<Product>;

        viewModelScope.launch {
            _bestProducts.emit(Resource.Loading())
            runBlocking {
                // Retrieve all products from the database
                allProducts = MainApp.database.productDao().getAllProductsByCategory(category.category)
            }
            viewModelScope.launch {
                _bestProducts.emit(Resource.Success(allProducts))
            }
        }

    }

}