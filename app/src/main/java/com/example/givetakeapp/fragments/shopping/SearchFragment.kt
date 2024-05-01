package com.example.givetakeapp.fragments.shopping
import android.app.Activity.RESULT_OK
import android.content.Intent
import com.example.givetakeapp.R
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.givetakeapp.MainApp
import com.example.givetakeapp.SharedData
import com.example.givetakeapp.activities.ShoppingActivity
import com.example.givetakeapp.data.Product
import com.example.givetakeapp.data.User
import com.example.givetakeapp.databinding.FragmentSearchBinding
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.util.UUID

class SearchFragment : Fragment(R.layout.fragment_search) {
    private lateinit var binding: FragmentSearchBinding
    private var imageUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonImagesPicker.setOnClickListener {
            openImagePicker()
        }
        binding.buttonColorPicker.setOnClickListener {
            saveNewProduct()
        }
    }

    private fun saveNewProduct(){
        val category = binding.spCategory.selectedItem.toString()
        val city = binding.spCity.selectedItem.toString()
        val description = binding.edDescription.text.toString()
       // val contant = binding.edName.text.toString()


        val product = Product(
            id = generateUUID(), // Generate unique ID for the product
            category = category, // set the category
            userEmail = SharedData.myVariable, // Set the user's email
            city = city, // Set the city
//            description= description,
            imagePath = imageUrl ?: "" //set image in base64
        )
        // Print the product details
        println(product)
        saveProduct(product)

    }
    fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }
    private fun saveProduct(product: Product) {
        runBlocking {
            //MainApp.database.productDao().deleteAllProducts()
            MainApp.database.productDao().insertProduct(product)
            // Retrieve all products from the database
            val allProducts = MainApp.database.productDao().getAllProducts()

            // Change navigation to shopping
            Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                // Make sure pressing back dont go back to login
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

        }
    }
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data
            selectedImageUri?.let { uri ->
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                imageUrl = bitmapToBase64(bitmap)
                println(imageUrl)
            }
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}


