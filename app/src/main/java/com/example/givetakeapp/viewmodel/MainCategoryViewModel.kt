package com.example.givetakeapp.viewmodel

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

    private val _allProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    private val pagingInfo = PagingInfo()
    val allProducts: StateFlow<Resource<List<Product>>> = _allProducts

    init {
        fetchAllProducts()
    }

    fun fetchAllProducts() {
        var allProducts: List<Product>;
        if (!pagingInfo.isPagingEnd) {
            viewModelScope.launch {
                _allProducts.emit(Resource.Loading())
                runBlocking {
                    //Retrieve all products from the database
                    allProducts = MainApp.database.productDao().getAllProducts()
                }
                pagingInfo.isPagingEnd = allProducts == pagingInfo.oldAllProducts
                pagingInfo.oldAllProducts = allProducts
                viewModelScope.launch {
                    _allProducts.emit(Resource.Success(allProducts))
                }
                pagingInfo.allProductsPage++
            }
        }
    }
}

internal data class PagingInfo(
    var allProductsPage: Long = 1,
    var oldAllProducts: List<Product> = emptyList(),
    var isPagingEnd: Boolean = false
)
