package com.example.givetakeapp.fragments.shopping

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.givetakeapp.R
import com.example.givetakeapp.databinding.FragmentProductDetailsBinding
import com.example.givetakeapp.util.hideBottomNavigationView
import com.example.givetakeapp.util.showBottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var binding: FragmentProductDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavigationView()
        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }

    private fun decodeBase64ToBitmap(base64String: String): Bitmap {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val product = args.product

        binding.buttonAllProducts.setOnClickListener {
            findNavController().navigate(R.id.action_productDetailsFragment_to_homeFragment)
        }
        lifecycleScope.launchWhenStarted {}
        binding.apply {
            tvProductName.text = product.category
            tvProductCity.text = product.city
            tvProductContat.text = product.userEmail
            val decodedImage = decodeBase64ToBitmap(product.imagePath)
            imgProduct.setImageBitmap(decodedImage)
            tvProductDescription.text = product.description
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}