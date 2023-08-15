//package com.project.restaurand_android
//
//
//import android.Manifest
//import android.app.Activity
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.location.Address
//import android.location.Geocoder
//import android.location.Location
//import android.location.LocationListener
//import android.location.LocationManager
//import android.os.Bundle
//import android.util.Log
//import android.widget.Button
//import android.widget.SearchView
//import android.widget.TextView
//import androidx.activity.ComponentActivity
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.lifecycleScope
//import androidx.navigation.findNavController
//import androidx.navigation.fragment.findNavController
//import com.project.restaurand_android.Maps
//import com.project.restaurand_android.R
//import com.project.restaurand_android.ui.theme.RestauRandAndroidTheme
//
//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.RequestBody.Companion.toRequestBody
//import org.json.JSONObject
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import org.json.JSONArray
//
//import java.util.Locale
//
//var userCity = ""
//
//class MainActivity : ComponentActivity() {
//    lateinit var searchView: SearchView
//    lateinit var textView: TextView
//    lateinit var mapsButton: Button
//
//    private lateinit var locationManager: LocationManager
//
//    private val locationListener = object : LocationListener {
//        override fun onLocationChanged(location: Location) {
//            // Since latitude and longitude has a mind of its own, set default lat long to Waterloo (43.4643, 80.5204)
//            val defaultLatitude = 43.4643
//            val defaultLongitude = -80.5204
//
//            // Reverse geocoding to get the address
//            val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
//            //val addresses = geocoder.getFromLocation(defaultLatitude, defaultLongitude, 1)
//
//            //Fetch address from location
//            geocoder.getFromLocation(defaultLatitude,defaultLongitude,1,object : Geocoder.GeocodeListener{
//                override fun onGeocode(addresses: MutableList<Address>) {
//                    val address = addresses[0]
//                    val cityName = address.locality // Get the city name
//
//                    // Do something with the city name
//                    Log.d("CDEBUG: ", "Addresses: $addresses")
//                    Log.d("CDEBUG: ", "City: $cityName")
//                    userCity = cityName
//                }
//                override fun onError(errorMessage: String?) {
//                    super.onError(errorMessage)
//                    Log.d("CDEBUG: ", "No address found")
//                }
//
//            })
//        }
//
//        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
//
//        override fun onProviderEnabled(provider: String) {}
//
//        override fun onProviderDisabled(provider: String) {}
//    }
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.layout)
//        searchView = findViewById(R.id.searchView)
//        textView = findViewById(R.id.textView)
//        mapsButton = findViewById(R.id.button)
//
//        // Initialize locationManager
//        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//        val isLocationPermissionGranted = checkLocationPermission(this)
//
//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//        } else {
//            startLocationUpdates()
//        }
////        if (!isLocationPermissionGranted) {
////            requestLocationPermission(this)
////        } else {
////            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
////            startLocationUpdates()
////        }
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                query?.let {
//                    lifecycleScope.launch {
//                        //val response = generateChatGPTResponse(it)
//                        //textView.text = response
//                    }
//                }
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                return false
//            }
//        })
//
//        mapsButton.setOnClickListener {
//            val intent = Intent(this@MainActivity, Maps::class.java)
//            startActivity(intent)
//        }
//    }
//
//    private fun startLocationUpdates() {
//        val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
//        if (ContextCompat.checkSelfPermission(this, locationPermission) == PackageManager.PERMISSION_GRANTED) {
//            locationManager.requestLocationUpdates(
//                LocationManager.GPS_PROVIDER,
//                0L,
//                0f,
//                locationListener
//            )
//        } else {
//            Log.d("CDEBUG: ", "Location Permission not granted")
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startLocationUpdates()
//            } else {
//                Log.d("CDEBUG: ", "Location permission denied")
//            }
//        }
//    }
//}
//
//private const val LOCATION_PERMISSION_REQUEST_CODE = 100
//fun checkLocationPermission(context: Context): Boolean {
//    val permissionResult = ContextCompat.checkSelfPermission(
//        context,
//        Manifest.permission.ACCESS_COARSE_LOCATION
//    )
//    return permissionResult == PackageManager.PERMISSION_GRANTED
//}
//
//private fun requestLocationPermission(context: Context) {
//    ActivityCompat.requestPermissions(
//        context as Activity,
//        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//        LOCATION_PERMISSION_REQUEST_CODE
//    )
//}
//
//
//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    RestauRandAndroidTheme {
//        Greeting("Android")
//    }
//}
