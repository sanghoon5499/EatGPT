package com.project.EatGPT

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.IOException


// override is used very often as Android provides a framework with many pre-defined classes
//   devs can extend or implement to fit our needs. To stay safe, we override these to use them.


class Maps : ComponentActivity(), OnMapReadyCallback {

    lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationManager: LocationManager
    private lateinit var businessName: String
    private lateinit var businessAddress: String

    // savedInstanceState is a Bundle type, which contains the saved state of the activity.
    // This is data saved from a previous instance of the activity (screen), such as when the
    //   the activity is destroyed and recreated (screen rotation), or other changes.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.maps)

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync(this@Maps)

        //locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Initialize the FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        businessName = intent.getStringExtra("BUSINESS_NAME").toString()
        businessAddress = intent.getStringExtra("BUSINESS_ADDRESS").toString()
        if (businessName != null || businessAddress != null) {
            // Use the businessName value in Maps activity
            Log.d("CDEBUG: MapsActivity", "Received business name: $businessName")
            Log.d("CDEBUG: MapsActivity", "Received business location: $businessAddress")
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()

        mapView.getMapAsync { googleMap ->
            // Configure the map
            googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            googleMap.uiSettings.isZoomControlsEnabled = true
        }
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        // Configure the map
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.uiSettings.isZoomControlsEnabled = true

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //val isLocationPermissionGranted = checkLocationPermission(this)

        // Get the user's current location and move the camera to that location
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                var businessLong = 0.0
                var businessLat = 0.0
                val geocoder = Geocoder(this) // 'this' is your context
                try {
                    val addresses: List<Address> = geocoder.getFromLocationName(businessAddress, 1) as List<Address>
                    if (addresses.isNotEmpty()) {
                        businessLong = addresses[0].latitude
                        businessLat = addresses[0].longitude
                    } else {
                        // No matching address found
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    // Handle IOException
                }

                Log.d("CDEBUG: ", "${businessLong}, ${businessLat}")

                val userLatLng = LatLng(businessLong, businessLat)
                val cameraPosition = CameraPosition.Builder()
                    .target(userLatLng)
                    .zoom(15f) // You can adjust the zoom level as needed
                    .build()

                googleMap.addMarker(MarkerOptions().position(userLatLng).title("Your Location"))
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            } else {
                Log.d("MapsActivity", "Last location is null.")
            }
        }
    }
}

