package com.example.givetakeapp.fragments.shopping

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import com.example.givetakeapp.activities.ShoppingActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.givetakeapp.MainApp
import com.example.givetakeapp.R
import com.example.givetakeapp.SharedData
import com.example.givetakeapp.data.Product
import com.example.givetakeapp.databinding.FragmentEditProductBinding
import com.example.givetakeapp.databinding.FragmentProductDetailsBinding
import com.example.givetakeapp.util.hideBottomNavigationView
import com.example.givetakeapp.util.showBottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

@AndroidEntryPoint
class EditProductFragment : Fragment() {
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var binding: FragmentEditProductBinding
    private var imageUrl: String = ""
    private lateinit var imageProduct: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavigationView()
        binding = FragmentEditProductBinding.inflate(inflater)
        return binding.root
    }
    private fun decodeBase64ToBitmap(base64String: String): Bitmap {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }
    private inner class FetchCitiesTask : AsyncTask<Void, Void, Pair<List<String>?, String?>>() {

        override fun doInBackground(vararg params: Void?): Pair<List<String>?, String?> {
            val cities = mutableListOf<String>()
            var selectedCity: String? = null
            try {
                val url =
                    URL("http://api.geonames.org/searchJSON?country=IL&username=liel&maxRows=10")
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                val bufferedReader =
                    BufferedReader(InputStreamReader(connection.inputStream))
                val stringBuilder = StringBuilder()
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                }
                bufferedReader.close()

                val response = stringBuilder.toString()
                val jsonResponse = JSONObject(response)
                val geonamesArray = jsonResponse.getJSONArray("geonames")

                for (i in 0 until geonamesArray.length()) {
                    val cityObject = geonamesArray.getJSONObject(i)
                    val cityName = cityObject.getString("name")
                    cities.add(cityName)
                    // Check if the current city matches the product's city
                    if (cityName == args.product.city) {
                        selectedCity = cityName
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return Pair(cities, selectedCity)
        }

        override fun onPostExecute(result: Pair<List<String>?, String?>) {
            super.onPostExecute(result)
            val (cities, selectedCity) = result
            if (cities != null) {
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    cities
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spCity.adapter = adapter
                // Set the initial selection based on the product's city
                val cityIndex = cities.indexOf(selectedCity)
                if (cityIndex != -1) {
                    binding.spCity.setSelection(cityIndex)
                }
            }
        }
    }


    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            selectedImageUri?.let { uri ->
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                imageUrl = bitmapToBase64(bitmap)
                println(imageUrl)
                imageProduct.setImageBitmap(bitmap)
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageProduct = view.findViewById(R.id.imageProduct)

        // Fetch cities and populate the spinner
        FetchCitiesTask().execute()

        // Set up image picker button
        binding.buttonImagesPicker.setOnClickListener {
            openImagePicker()
        }

        // Get the product details from args
        val product = args.product

        // Set up save button click listener
        binding.buttonSave.setOnClickListener {

            saveNewProduct()
            // Navigate to the cart fragment
            findNavController().navigate(R.id.action_editProductFragment_to_cartFragment)
        }

        // Populate UI with product details
        binding.apply {
            // Load product image
            Glide.with(this@EditProductFragment)
                .load(decodeBase64ToBitmap(product.imagePath))
                .error(ColorDrawable(Color.BLACK))
                .into(imageProduct)

            // Set product description
            edDescription.setText(product.description)

            // Set product contact
            edName.setText(product.userEmail)
        }

        // Populate category spinner and set the initial selection
        val categories = resources.getStringArray(R.array.category_options)
        val categoryIndex = categories.indexOf(product.category)
        if (categoryIndex != -1) {
            binding.spCategory.setSelection(categoryIndex)
        }
    }
    private fun saveNewProduct(){
        val category = binding.spCategory.selectedItem.toString()
        val city = binding.spCity.selectedItem.toString()
        val description = binding.edDescription.text.toString()
        val contant = binding.edName.text.toString()


        val product = Product(
            id = args.product.id, // Generate unique ID for the product
            category = category, // set the category
            userEmail = contant, // Set the user's email
            city = city, // Set the city
            description= description,
            imagePath = imageUrl
        )
        saveProduct(product)

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
    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}