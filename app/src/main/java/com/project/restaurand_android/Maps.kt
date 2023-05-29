package com.project.restaurand_android

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.project.restaurand_android.ui.theme.RestauRandAndroidTheme



// override is used very often as Android provides a framework with many pre-defined classes
//   devs can extend or implement to fit our needs. To stay safe, we override these to use them.


class Maps : ComponentActivity() {

    lateinit var mapView: MapView

    // savedInstanceState is a Bundle type, which contains the saved state of the activity.
    // This is data saved from a previous instance of the activity (screen), such as when the
    //   the activity is destroyed and recreated (screen rotation), or other changes.
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("TEST: ", "bruh 2")
        // "super" is in reference to the page we want to initialize. "super" = "superclass"
        // This line is only called once
        super.onCreate(savedInstanceState)

        // sets the UI layout as specified in the corresponding layout.xml file
        setContentView(R.layout.maps)
        // you can search for ids in maps.xml by: "name = findViewById(R.id.[idname])"

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
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
}

