package com.jetpack.googlemapmultiplemarker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MarkerOptions
import com.jetpack.googlemapmultiplemarker.ui.theme.GoogleMapMultipleMarkerTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GoogleMapMultipleMarkerTheme {
                Surface(color = MaterialTheme.colors.background) {
                    GoogleMapMultipleMarker()
                }
            }
        }
    }
}

@Composable
fun GoogleMapMultipleMarker() {
    val mapview = rememberMapViewWithLifeCycle()
    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            AndroidView(
                { mapview }
            ) { mapView ->
                CoroutineScope(Dispatchers.Main).launch {
                    mapView.getMapAsync {
                        it.mapType = 1
                        it.uiSettings.isZoomControlsEnabled = true
                        val mark1 = LatLng(17.385, 78.4867) //Hyd
                        val mark2 = LatLng(18.5204, 73.8567) //Pune
                        val mark3 = LatLng(12.9716, 77.5946) //Bang

                        it.moveCamera(CameraUpdateFactory.newLatLngZoom(mark1, 6f))
                        val markerOption = MarkerOptions()
                            .title("Hyderabad")
                            .position(mark1)
                        val markerOptions2 = MarkerOptions()
                            .title("Pune")
                            .position(mark2)
                        val markerOptions3 = MarkerOptions()
                            .title("Bangalore")
                            .position(mark3)

                        it.addMarker(markerOption)
                        it.addMarker(markerOptions2)
                        it.addMarker(markerOptions3)
                    }
                }
            }
        }
    }
}

@Composable
fun rememberMapLifeCycleObserver(
    mapView: MapView
): LifecycleEventObserver = remember(mapView) {
    LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
            Lifecycle.Event.ON_START -> mapView.onStart()
            Lifecycle.Event.ON_RESUME -> mapView.onResume()
            Lifecycle.Event.ON_PAUSE -> mapView.onPause()
            Lifecycle.Event.ON_STOP -> mapView.onStop()
            Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
            else -> throw IllegalStateException()
        }
    }
}

@Composable
fun rememberMapViewWithLifeCycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = com.google.maps.android.ktx.R.id.map_frame
        }
    }

    val lifecycleObserver = rememberMapLifeCycleObserver(mapView = mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    return mapView
}
























