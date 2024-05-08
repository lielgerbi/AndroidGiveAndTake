package com.example.givetakeapp.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.givetakeapp.R
import com.example.givetakeapp.adapters.MyProductsAdapter
import com.example.givetakeapp.databinding.FragmentMyProductsBinding
import com.example.givetakeapp.util.Resource
import com.example.givetakeapp.util.VerticalItemDecoration
import com.example.givetakeapp.util.showBottomNavigationView
import com.example.givetakeapp.viewmodel.MyProductsViewModel
import kotlinx.coroutines.flow.collectLatest

class MyProductsFragment : Fragment(R.layout.fragment_my_products) {
    private lateinit var binding: FragmentMyProductsBinding
    private val myProductsAdapter by lazy { MyProductsAdapter() }
    private val viewModel by activityViewModels<MyProductsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyProductsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMyProductsRv()

        myProductsAdapter.onProductClick = {
            val b = Bundle().apply {
                putParcelable("product", it)
            }
            findNavController().navigate(R.id.action_myProductsFragment_to_editProductFragment, b)
        }

        myProductsAdapter.onMinusClick = {
            viewModel.deleteProduct(it)
            findNavController().navigate(R.id.action_myProductsFragment_to_homeFragment)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.myProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.progressbarMyProducts.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.progressbarMyProducts.visibility = View.INVISIBLE
                        if (it.data!!.isEmpty()) {
                            showEmptyMyProducts()
                            hideOtherViews()
                        } else {
                            hideEmptyMyProducts()
                            showOtherViews()
                            myProductsAdapter.differ.submitList(it.data)
                        }
                    }

                    is Resource.Error -> {
                        binding.progressbarMyProducts.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun showOtherViews() {
        binding.apply {
            rvMyProducts.visibility = View.VISIBLE
        }
    }

    private fun hideOtherViews() {
        binding.apply {
            rvMyProducts.visibility = View.GONE
        }
    }

    private fun hideEmptyMyProducts() {
        binding.apply {
            layoutMyProductsEmpty.visibility = View.GONE
        }
    }

    private fun showEmptyMyProducts() {
        binding.apply {
            layoutMyProductsEmpty.visibility = View.VISIBLE
        }
    }

    private fun setupMyProductsRv() {
        binding.rvMyProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = myProductsAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMyProducts()
        showBottomNavigationView()
    }
}