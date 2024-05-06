package com.example.givetakeapp.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.givetakeapp.R
import com.example.givetakeapp.adapters.AllProductsAdapter
import com.example.givetakeapp.databinding.FragmentBaseCategoryBinding
import com.example.givetakeapp.util.showBottomNavigationView

open class BaseCategoryFragment: Fragment(R.layout.fragment_base_category) {
    private lateinit var binding: FragmentBaseCategoryBinding
    protected val  allProductsAdapter: AllProductsAdapter by lazy { AllProductsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBestProductsRv()
        allProductsAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product",it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        binding.nestedScrollBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY){
                onAllProductsPagingRequest()
            }
        })
    }

    fun showAllProductsLoading(){
        binding.bestProductsProgressBar.visibility = View.VISIBLE
    }

    fun hideAllProductsLoading(){
        binding.bestProductsProgressBar.visibility = View.GONE
    }

    open fun onAllProductsPagingRequest(){
    }

    private fun setupBestProductsRv() {
        binding.rvBestProducts.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = allProductsAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}