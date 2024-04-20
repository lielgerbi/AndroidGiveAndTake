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
import com.example.givetakeapp.data.Product
import com.example.givetakeapp.data.User
import com.example.givetakeapp.databinding.FragmentSearchBinding
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream

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
        val category = binding.edCategory.text.toString()
        val description = binding.edDescription.text.toString()

        val product = Product(
            id = category, // Generate your own unique ID for the product
            category = category,
            userEmail = "user_email@example.com", // Set the user's email
            city = "city_name", // Set the city
            imagePath = imageUrl ?: ""
        )
        // Print the product details
        println(product)
        saveProduct(product)

    }
    private fun saveProduct(product: Product) {
        runBlocking {
            MainApp.database.productDao().insertProduct(product)
            // Retrieve all products from the database
            val allProducts = MainApp.database.productDao().getAllProducts()

            // Print the list of products
            allProducts.forEach { println(it) }

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


