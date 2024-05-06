package com.example.givetakeapp.fragments.shopping

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.givetakeapp.MainApp
import com.example.givetakeapp.R
import com.example.givetakeapp.activities.ShoppingActivity
import com.example.givetakeapp.data.Product
import com.example.givetakeapp.databinding.FragmentEditProductBinding
import com.example.givetakeapp.util.hideBottomNavigationView
import com.example.givetakeapp.util.showBottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

@AndroidEntryPoint
class EditProductFragment : Fragment() {
    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var binding: FragmentEditProductBinding
    private var imageStr: String = ""
    private lateinit var imageProduct: ImageView
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavigationView()
        binding = FragmentEditProductBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                uri?.let {
                    val inputStream = requireContext().contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    imageStr = bitmapToBase64(bitmap)
                    imageProduct.setImageBitmap(bitmap)
                }
            }

        imageProduct = binding.imageProduct
        FetchCitiesTask().execute()
        binding.buttonImagesPicker.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        val product = args.product
        imageStr = product.imagePath

        binding.buttonSave.setOnClickListener {
            saveNewProduct()
        }

        binding.apply {
            Glide.with(this@EditProductFragment).load(decodeBase64ToBitmap(product.imagePath))
                .error(ColorDrawable(Color.BLACK)).into(imageProduct)
            edDescription.setText(product.description)
            edName.setText(product.userEmail)
        }

        val categories = resources.getStringArray(R.array.category_options)
        val categoryIndex = categories.indexOf(product.category)
        if (categoryIndex != -1) {
            binding.spCategory.setSelection(categoryIndex)
        }
    }

    private fun decodeBase64ToBitmap(base64String: String): Bitmap {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    @SuppressLint("StaticFieldLeak")
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

    private fun saveNewProduct() {
        val product = Product(
            id = args.product.id,
            category = binding.spCategory.selectedItem.toString(),
            userEmail = binding.edName.text.toString(),
            city = binding.spCity.selectedItem.toString(),
            description = binding.edDescription.text.toString(),
            imagePath = imageStr
        )
        saveProduct(product)
    }

    private fun saveProduct(product: Product) {
        runBlocking {
            MainApp.database.productDao().insertProduct(product)
            Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}