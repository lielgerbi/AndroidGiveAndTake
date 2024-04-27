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

    private val _deleteDialog = MutableSharedFlow<Product>()
    val deleteDialog = _deleteDialog.asSharedFlow()

    private var cartProductDocuments = emptyList<DocumentSnapshot>()


    fun deleteCartProduct(cartProduct: Product) {
        val index = cartProducts.value.data?.indexOf(cartProduct)
        if (index != null && index != -1) {
//            val documentId = cartProductDocuments[index].id
//            firestore.collection("user").document(auth.uid!!).collection("cart")
//                .document(documentId).delete()

        }
    }



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





}