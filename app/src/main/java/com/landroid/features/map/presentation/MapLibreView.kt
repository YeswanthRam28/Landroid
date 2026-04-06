// app/src/main/java/com/landroid/features/map/presentation/MapLibreView.kt
package com.landroid.features.map.presentation

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.landroid.shared.models.Parcel
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.FillLayer
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource

@Composable
fun MapLibreView(
    parcel: Parcel?,
    activeLayer: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    MapLibre.getInstance(context)

    val mapView = remember { MapView(context) }

    DisposableEffect(lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) = mapView.onCreate(null)
            override fun onStart(owner: LifecycleOwner) = mapView.onStart()
            override fun onResume(owner: LifecycleOwner) = mapView.onResume()
            override fun onPause(owner: LifecycleOwner) = mapView.onPause()
            override fun onStop(owner: LifecycleOwner) = mapView.onStop()
            override fun onDestroy(owner: LifecycleOwner) = mapView.onDestroy()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier,
        update = { mv ->
            mv.getMapAsync { map ->
                val esriSatelliteStyle = """
                {
                  "version": 8,
                  "sources": {
                    "esri": {
                      "type": "raster",
                      "tiles": ["https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}"],
                      "tileSize": 256
                    }
                  },
                  "layers": [
                    {
                      "id": "satellite",
                      "type": "raster",
                      "source": "esri"
                    }
                  ]
                }
                """.trimIndent()

                map.setStyle(Style.Builder().fromJson(esriSatelliteStyle)) { style ->
                    
                    // HACKATHON: Load the asset GeoJSON directly and snap the camera bounds
                    map.cameraPosition = CameraPosition.Builder()
                        .target(LatLng(10.4345, 77.2920))
                        .zoom(16.0)
                        .build()

                    runCatching {
                        val isNdvi = activeLayer == "NDVI"
                        val fillColor = if (isNdvi) Color.argb(160, 46, 204, 113) else Color.argb(40, 26, 71, 49)
                        
                        style.addSource(GeoJsonSource("boundary-source", java.net.URI("asset://boundary.geojson")))
                        style.addLayer(
                            FillLayer("boundary-fill", "boundary-source").withProperties(
                                PropertyFactory.fillColor(fillColor),
                                PropertyFactory.fillOpacity(if (isNdvi) 0.6f else 0.2f)
                            )
                        )
                        style.addLayer(
                            LineLayer("boundary-line", "boundary-source").withProperties(
                                PropertyFactory.lineColor(Color.rgb(243, 156, 18)),
                                PropertyFactory.lineWidth(if (isNdvi) 2f else 4f)
                            )
                        )

                        // OSMNx Live API Layers
                        val backendUrl = com.landroid.BuildConfig.BACKEND_API_URL
                        val pid = parcel?.id ?: "demo"

                        style.addSource(GeoJsonSource("water-source", java.net.URI("${backendUrl}api/parcels/$pid/water")))
                        val waterColor = if (isNdvi) Color.argb(100, 41, 128, 185) else Color.argb(180, 52, 152, 219)
                        style.addLayer(
                            FillLayer("water-fill", "water-source").withProperties(
                                PropertyFactory.fillColor(waterColor),
                                PropertyFactory.fillOpacity(0.6f)
                            )
                        )

                        style.addSource(GeoJsonSource("roads-source", java.net.URI("${backendUrl}api/parcels/$pid/roads")))
                        val roadColor = if (isNdvi) Color.argb(120, 189, 195, 199) else Color.argb(180, 236, 240, 241)
                        style.addLayer(
                            LineLayer("roads-line", "roads-source").withProperties(
                                PropertyFactory.lineColor(roadColor),
                                PropertyFactory.lineWidth(1.5f)
                            )
                        )

                    }

                    parcel?.let { p ->
                        // Override camera if parcel has valid coordinates
                        if (p.centroidLat != 0.0 && p.centroidLng != 0.0) {
                            map.cameraPosition = CameraPosition.Builder()
                                .target(LatLng(p.centroidLat, p.centroidLng))
                                .zoom(16.0)
                                .build()
                        }
                    }
                }
            }
        }
    )
}
