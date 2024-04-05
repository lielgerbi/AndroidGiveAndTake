package com.example.givetakeapp.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.givetakeapp.R
import com.example.givetakeapp.databinding.ActivityShoppingBinding
import com.example.givetakeapp.util.Resource
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {

    val binding by lazy {
        ActivityShoppingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navController = findNavController(R.id.shoppingHostFragment)
        binding.bottomNavigation.setupWithNavController(navController)

//        lifecycleScope.launchWhenStarted {
//            viewModel.cartProducts.collectLatest {
//                when (it) {
//                    is Resource.Success -> {
//                        val count = it.data?.size ?: 0
//                        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
//                        bottomNavigation.getOrCreateBadge(R.id.cartFragment).apply {
//                            number = count
//                            backgroundColor = resources.getColor(R.color.g_blue)
//                        }
//                    }
//                    else -> Unit
//                }
//            }
//        }
    }

}