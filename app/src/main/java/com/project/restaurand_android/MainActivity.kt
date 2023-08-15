package com.project.restaurand_android


import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.gson.annotations.SerializedName
import com.project.restaurand_android.ui.theme.RestauRandAndroidTheme
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Locale



var userCity = ""

class MainActivity : ComponentActivity() {
    lateinit var searchView: SearchView
    lateinit var textView: TextView
    lateinit var mapsButton: Button

    private lateinit var locationManager: LocationManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout)
        searchView = findViewById(R.id.searchView)
        textView = findViewById(R.id.textView)
        mapsButton = findViewById(R.id.button)

        // Initialize locationManager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        // Request a single location update
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestSingleUpdate(
                LocationManager.GPS_PROVIDER,
                locationListener,
                null
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    lifecycleScope.launch {
                        val apiKey = "AIzaSyBMCbfKMOQmplUNvOiHNBalzBiXXabRG2c"
                        val placesApi = Retrofit.Builder()
                            .baseUrl("https://maps.googleapis.com/maps/api/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(OkHttpClient())
                            .build()
                            .create(PlacesApi::class.java)

                        try {
                            val response = placesApi.searchPlaces(it, apiKey)
                            if (response.isSuccessful) {
                                val places = response.body()?.results
                                val placesList: MutableList<String> = mutableListOf()
                                val forLoopLimiter = places?.size?.minus(1)
                                for (i in 0..(forLoopLimiter ?: 0)) {
                                    placesList.add(places?.get(i)?.name.toString())
                                }
                                val placesArrayJoined = placesList.joinToString(separator = "\n")
                                textView.text = placesArrayJoined

                                //val response = generateChatGPTResponse(query)
                                //textView.text = response
                            } else {
                                Log.e("API Error", "Response code: ${response.code()}")
                            }
                        } catch (e: Exception) {
                            Log.e("API Error", e.message ?: "Unknown error")
                        }
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        mapsButton.setOnClickListener {
            val intent = Intent(this@MainActivity, Maps::class.java)
            startActivity(intent)
        }
    }

    data class Place(val name: String, val address: String)

    interface PlacesApi {
        @GET("place/textsearch/json")
        suspend fun searchPlaces(
            @Query("query") query: String,
            @Query("key") apiKey: String
        ): Response<PlaceResponse>
    }

    data class PlaceResponse(
        @SerializedName("results") val results: List<Place>,
        // Add other necessary fields from the response
    )

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            // Get the latitude and longitude
            val latitude = location.latitude
            val longitude = location.longitude

            Log.d("CDEBUG: ", "latitude: $latitude")
            Log.d("CDEBUG: ", "longitude: $longitude")

            // Reverse geocoding to get the address
            val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val cityName = address.locality // Get the city name

                    // Do something with the city name
                    Log.d("CDEBUG: ", "City: $cityName")
                    userCity = cityName
                } else {
                    Log.d("CDEBUG: ", "No address found")
                }
            }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

        override fun onProviderEnabled(provider: String) {}

        override fun onProviderDisabled(provider: String) {}
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    private fun startLocationUpdates() {
        val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(this, locationPermission) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0L,
                0f,
                locationListener
            )
        } else {
            Log.d("CDEBUG: ", "Location Permission not granted")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates()
            } else {
                Log.d("CDEBUG: ", "Location permission denied")
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RestauRandAndroidTheme {
        Greeting("Android")
    }
}


//suspend fun generateChatGPTResponse(prompt: String): String {
//    val apiKey = "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"
//    val apiUrl = "https://api.openai.com/v1/chat/completions"
//
//    // tuned prompts will have instruction texts appended to the user's prompt in order to
//    //   keep the responses in a predictable format.
////    val tunedPrompt =
////        prompt + ". " +
////                "I'm located in the city of: ${userCity}." +
////                "Generate 10 such locations in an array format." +
////                "Exclude fast food chains." +
////                "Do not include any other text."
//    val tunedPrompt =
//        prompt + ". " +
//                "I'm located in the city of: $userCity."
//
//    val requestBodyJson = JSONObject()
//        .put("model", "gpt-3.5-turbo")
//        .put("messages", JSONArray().put(
//            JSONObject()
//            .put("role", "user")
//            .put("content", tunedPrompt)))
//
//    val client = OkHttpClient()
//    val mediaType = "application/json".toMediaType()
//    val requestBody = requestBodyJson.toString().toRequestBody(mediaType)
//
//    val request = Request.Builder()
//        .url(apiUrl)
//        .post(requestBody)
//        .header("Authorization", "Bearer $apiKey")
//        .build()
//
//    return withContext(Dispatchers.IO) {
//        val response = client.newCall(request).execute()
//        val responseBody = response.body?.string()
//
//        Log.d("CDEBUG response: ", response.toString())
//        Log.d("CDEBUG responseBody: ", responseBody.toString())
//
//        if (response.isSuccessful && !responseBody.isNullOrBlank()) {
//            val jsonResponse = JSONObject(responseBody)
//            val choices = jsonResponse.getJSONArray("choices")
//
//            if (choices.length() > 0) {
//                val firstChoice = choices.getJSONObject(0)
//                val message = firstChoice.getJSONObject("message")
//                val content = message.getString("content")
//                return@withContext content
//            }
//        }
//
//        return@withContext "Failed to generate a response."
//    }.replace("\\r", "").replace("\\n", "\n")
//}
