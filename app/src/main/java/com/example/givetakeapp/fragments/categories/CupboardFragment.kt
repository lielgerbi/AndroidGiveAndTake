package com.example.givetakeapp.fragments.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.givetakeapp.data.Category
import com.example.givetakeapp.util.Resource
import com.example.givetakeapp.viewmodel.CategoryViewModel
import com.example.givetakeapp.viewmodel.BaseCategoryViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest


class CupboardFragment: BaseCategoryFragment() {
    val viewModel by viewModels<CategoryViewModel> {
        BaseCategoryViewModelFactory(Category.Cupboard)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.allProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showAllProductsLoading()
                    }
                    is Resource.Success -> {
                        allProductsAdapter.differ.submitList(it.data)
                        hideAllProductsLoading()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG)
                            .show()
                        hideAllProductsLoading()
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onAllProductsPagingRequest() {

    }
}