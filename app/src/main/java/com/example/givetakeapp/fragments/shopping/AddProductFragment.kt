package com.example.givetakeapp.fragments.shopping

import android.annotation.SuppressLint
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
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.util.UUID
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import android.os.AsyncTask
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.example.givetakeapp.databinding.FragmentAddProductBinding
import com.example.givetakeapp.model.ProductsModel

class AddProductFragment : Fragment(R.layout.fragment_add_product) {
    private lateinit var binding: FragmentAddProductBinding
    private var imageStr: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddProductBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Call API to fetch cities
        FetchCitiesTask().execute()

        binding.buttonImagesPicker.setOnClickListener {
            openImagePicker()
        }
        binding.buttonColorPicker.setOnClickListener {
            saveNewProduct()
        }
    }

    private inner class FetchCitiesTask : AsyncTask<Void, Void, List<String>>() {

        override fun doInBackground(vararg params: Void?): List<String>? {
            val cities = mutableListOf<String>()
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
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return cities
        }

        override fun onPostExecute(result: List<String>?) {
            super.onPostExecute(result)
            if (result != null) {
                val adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    result
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spCity.adapter = adapter
            }
        }
    }

    private fun saveNewProduct() {
        val product = Product(
            id = generateUUID(),
            category = binding.spCategory.selectedItem.toString(),
            userEmail = SharedData.myVariable,
            city = binding.spCity.selectedItem.toString(),
            description = binding.edDescription.text.toString(),
            imagePath = imageStr ?: ""
        )
        println(product)
        saveProduct(product)
    }

    fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }

    private fun saveProduct(product: Product) {
        ProductsModel.instance.insertProduct(product) {
            // Change navigation to shopping
            findNavController().navigate(R.id.action_addProductFragment_to_homeFragment)
        }
    }

    @SuppressLint("IntentReset")
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
                imageStr = bitmapToBase64(bitmap)
                println(imageStr)
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


