//import android.Manifest
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.location.LocationManager
//import android.os.Bundle
//import android.util.Log
//import android.widget.SearchView
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.lifecycleScope
//import com.project.restaurand_android.LOCATION_PERMISSION_REQUEST_CODE
//import com.project.restaurand_android.Maps
//import com.project.restaurand_android.R
//import com.project.restaurand_android.checkLocationPermission
//import com.project.restaurand_android.userCity
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.RequestBody.Companion.toRequestBody
//import org.json.JSONArray
//import org.json.JSONObject
//
//suspend fun generateChatGPTResponse(prompt: String): String {
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
//

//
//override fun onCreate(savedInstanceState: Bundle?) {
//    super.onCreate(savedInstanceState)
//    setContentView(R.layout.layout)
//    searchView = findViewById(R.id.searchView)
//    textView = findViewById(R.id.textView)
//    mapsButton = findViewById(R.id.button)
//
//    // Initialize locationManager
//    locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//    val isLocationPermissionGranted = checkLocationPermission(this)
//
//    if (ContextCompat.checkSelfPermission(
//            this,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        ) != PackageManager.PERMISSION_GRANTED
//    ) {
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//            LOCATION_PERMISSION_REQUEST_CODE
//        )
//    } else {
//        startLocationUpdates()
//    }
////        if (!isLocationPermissionGranted) {
////            requestLocationPermission(this)
////        } else {
////            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
////            startLocationUpdates()
////        }
//
//    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//        override fun onQueryTextSubmit(query: String?): Boolean {
//            query?.let {
//                lifecycleScope.launch {
//                    val response = generateChatGPTResponse(it)
//                    textView.text = response
//                }
//            }
//            return false
//        }
//
//        override fun onQueryTextChange(newText: String?): Boolean {
//            return false
//        }
//    })
//
//    mapsButton.setOnClickListener {
//        val intent = Intent(this@MainActivity, Maps::class.java)
//        startActivity(intent)
//    }
//}