package com.example.givetakeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.givetakeapp.data.Category
import com.example.givetakeapp.viewmodel.CategoryViewModel

class BaseCategoryViewModelFactoryFactory(
    private val category: Category
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CategoryViewModel(category) as T
    }
}