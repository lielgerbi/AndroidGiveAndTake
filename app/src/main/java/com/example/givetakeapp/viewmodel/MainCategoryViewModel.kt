package com.example.givetakeapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.givetakeapp.MainApp
import com.example.givetakeapp.data.Product
import com.example.givetakeapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
) : ViewModel() {


    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts: StateFlow<Resource<List<Product>>> = _bestProducts

    private val pagingInfo = PagingInfo()

    init {
        fetchBestProducts()
    }
    fun fetchBestProducts() {
        var allProducts: List<Product>;
       if (!pagingInfo.isPagingEnd) {
            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())
                runBlocking {
                     //Retrieve all products from the database
                     allProducts = MainApp.database.productDao().getAllProducts()
                }
                pagingInfo.isPagingEnd = allProducts == pagingInfo.oldBestProducts
                pagingInfo.oldBestProducts = allProducts
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Success(allProducts))
                }
                pagingInfo.bestProductsPage++
            }
        }
    }
}

internal data class PagingInfo(
    var bestProductsPage: Long = 1,
    var oldBestProducts: List<Product> = emptyList(),
    var isPagingEnd: Boolean = false
)
