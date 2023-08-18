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
import android.view.View
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import org.w3c.dom.Text
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Locale


var userCity = ""

class MainActivity : ComponentActivity() {
    // Set variables here
    //  - will need to find a method to use recyclers for buttons and searchResults
    //      this will be even more important when ratings, price, etc are added too
    lateinit var searchView: SearchView
    lateinit var textView: TextView
    lateinit var searchResult1: TextView
    lateinit var searchResult2: TextView
    lateinit var searchResult3: TextView
    lateinit var button1: Button
    lateinit var button2: Button
    lateinit var button3: Button
    val initialSize = 10
    var topThreeNames: Array<String> = Array(initialSize) { "" }
    var topThreeAddress: Array<String> = Array(initialSize) { "" }

    lateinit var searchResultArray: Array<TextView>

    private lateinit var locationManager: LocationManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Grab layout.xml values and load them into our .kt file for use
        setContentView(R.layout.layout)
        searchView = findViewById(R.id.searchView)
        textView = findViewById(R.id.textView)
        button1 = findViewById(R.id.button1)
        button2 = findViewById(R.id.button2)
        button3 = findViewById(R.id.button3)
        searchResult1 = findViewById(R.id.searchResult1)
        searchResult2 = findViewById(R.id.searchResult2)
        searchResult3 = findViewById(R.id.searchResult3)
        searchResultArray = arrayOf(searchResult1, searchResult2, searchResult3)


        // Initialize locationManager + FINE/COARSE LOCATION + PERMISSION STUFF
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

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

        // The searchView textListener: WHEN USER SEARCHES IN TEXT BAR, THIS CODE RUNS
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    lifecycleScope.launch {
                        // Build info required to send out the API request
                        val apiKey = "AIzaSyBMCbfKMOQmplUNvOiHNBalzBiXXabRG2c"
                        val placesApi = Retrofit.Builder()
                            .baseUrl("https://maps.googleapis.com/maps/api/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(OkHttpClient())
                            .build()
                            .create(PlacesApi::class.java)

                        try {
                            // Generate the response using ChatGPT.
                            // Add "cuisine" to the response so Google Places doesn't return
                            //  something like "American Eagle" instead of "Milestones"
                            val response = generateChatGPTResponse(it) + " cuisine"

                            // Use the generated response as the query for places search
                            val placesResponse = placesApi.searchPlaces(response, apiKey)
                            if (placesResponse.isSuccessful) {
                                val places = placesResponse.body()?.results
//                                val placesArrayJoined = places?.joinToString(separator = "\n") { it.name }
//                                textView.text = placesArrayJoined

                                // Add names and address to list to send them to Maps.kt if user clicks on go to maps button
                                for (i in 0..2) {
                                    val tempName = places?.get(i)?.name
                                    val tempAddress = places?.get(i)?.address
                                    if (tempName != null) {
                                        topThreeNames[i] = tempName
                                        searchResultArray[i].text = tempName
                                    }
                                    if (tempAddress != null) {
                                        topThreeAddress[i] = tempAddress
                                    }
                                }
                            } else {
                                Log.e("API Error", "Response code: ${placesResponse.code()}")
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

        // For button clicks, and other clicks but for now it's just button clicks
        //  - likewise, these will need to be generalized to x number of stuff
        val onClickListener = View.OnClickListener { view ->
            val intent = Intent(this@MainActivity, Maps::class.java)

            // Determine the index based on the clicked button's ID
            val index = when (view.id) {
                R.id.button1 -> 0
                R.id.button2 -> 1
                R.id.button3 -> 2
                else -> -1 // Default value or handle other cases
            }

            if (index in 0 .. topThreeNames.size - 1) {
                intent.putExtra("BUSINESS_NAME", topThreeNames[index])
                intent.putExtra("BUSINESS_ADDRESS", topThreeAddress[index])
                startActivity(intent)
            }
        }
        button1.setOnClickListener(onClickListener)
        button2.setOnClickListener(onClickListener)
        button3.setOnClickListener(onClickListener)
    }

    // Google Places API Init

    // Here, we specify which info from the returned JSON that we want
    data class Place(
        val name: String,
        @SerializedName("formatted_address") val address: String,
        @SerializedName("price_level") val priceLevel: Int,
        val rating: Double
    )

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

    // Runs when the location has changed
    // For testing purposes only; not being used anywhere
    //  - Q: If this isn't being used, how does app know if you changed locations?
    //  - A: startLocationUpdates handles that task
    //  - Q: Why is this code still hanging around?
    //  - A: Just in case I want to use parts of it. It will be removed when project is completed.
    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            // Get the latitude and longitude
            val latitude = location.latitude
            val longitude = location.longitude

            // Reverse geocoding to get the address
            val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)


            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val cityName = address.locality
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

    // Asks locationManager for location
    private fun startLocationUpdates() {
        val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(this, locationPermission) == PackageManager.PERMISSION_GRANTED) {
            // minTimeMs:    0L ("L" == "Long" literal), use this to update every x ms
            // minDistanceM: 0f ("f" == "float"),        use this to update every x meters travelled
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                1000L,
                0f,
                locationListener
            )
        } else {
            Log.d("CDEBUG: ", "Location Permission not granted")
        }
    }

    // Actions based upon if the user agreed to location permissions or not
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

// @composable, @preview: idk what this is it came with the template
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

// ChatGPI API call
suspend fun generateChatGPTResponse(prompt: String): String {
    val apiKey = "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"
    val apiUrl = "https://api.openai.com/v1/chat/completions"

    // tuned prompts will have instruction texts appended to the user's prompt in order to
    //   keep the responses in a predictable format.
    val tunedPrompt =
                "Determine the cuisine that matches the most based on the upcoming prompt. " +
                "Your answer will be exactly one word, no more, no less. " +
                "This word will be the name of the cuisine that you have determined." +
                "Here is the prompt: " + prompt

    val requestBodyJson = JSONObject()
        .put("model", "gpt-3.5-turbo")
        .put("messages", JSONArray().put(
            JSONObject()
            .put("role", "user")
            .put("content", tunedPrompt)))

    val client = OkHttpClient()
    val mediaType = "application/json".toMediaType()
    val requestBody = requestBodyJson.toString().toRequestBody(mediaType)

    val request = Request.Builder()
        .url(apiUrl)
        .post(requestBody)
        .header("Authorization", "Bearer $apiKey")
        .build()

    return withContext(Dispatchers.IO) {
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()

        Log.d("CDEBUG response: ", response.toString())
        Log.d("CDEBUG responseBody: ", responseBody.toString())

        if (response.isSuccessful && !responseBody.isNullOrBlank()) {
            val jsonResponse = JSONObject(responseBody)
            val choices = jsonResponse.getJSONArray("choices")

            if (choices.length() > 0) {
                val firstChoice = choices.getJSONObject(0)
                val message = firstChoice.getJSONObject("message")
                val content = message.getString("content")
                return@withContext content
            }
        }

        return@withContext "Failed to generate a response."
    }.replace("\\r", "").replace("\\n", "\n")
}
