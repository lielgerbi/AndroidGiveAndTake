package com.example.givetakeapp.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.givetakeapp.R
import com.example.givetakeapp.activities.ShoppingActivity
import com.example.givetakeapp.databinding.FragmentIntrodcutionBinding
import com.example.givetakeapp.helper.UserManager
import com.example.givetakeapp.SharedData


class IntroductionFragment:Fragment(R.layout.fragment_introdcution) {
    private lateinit var binding: FragmentIntrodcutionBinding
    private lateinit var userManager: UserManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntrodcutionBinding.inflate(inflater)
        userManager = UserManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (userManager.isUserLoggedIn()){
            SharedData.myVariable =userManager.getUserEmail().toString()
            // Change navigation to shopping
            Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                // Make sure pressing back dont go back to login
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

        }
        else{
            binding.buttonStart.setOnClickListener {
                findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment2)
           }
        }

    }
}