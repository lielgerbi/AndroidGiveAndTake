package com.example.givetakeapp.fragments.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.givetakeapp.data.Category
import com.example.givetakeapp.util.Resource
import com.example.givetakeapp.viewmodel.CategoryViewModel
import com.example.givetakeapp.viewmodel.BaseCategoryViewModelFactoryFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest


class CupboardFragment: BaseCategoryFragment() {
    val viewModel by viewModels<CategoryViewModel> {
        BaseCategoryViewModelFactoryFactory(Category.Cupboard)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showBestProductsLoading()
                    }
                    is Resource.Success -> {
                        bestProductsAdapter.differ.submitList(it.data)
                        hideBestProductsLoading()
                    }
                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG)
                            .show()
                        hideBestProductsLoading()
                    }
                    else -> Unit
                }
            }
        }
    }

    override fun onBestProductsPagingRequest() {

    }
}