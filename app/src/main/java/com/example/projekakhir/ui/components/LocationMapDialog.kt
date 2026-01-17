package com.example.projekakhir.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun LocationMapDialog(
    latitude: Double,
    longitude: Double,
    studentName: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    
    // Initialize OSMDroid config
    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .clip(RoundedCornerShape(16.dp)),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    factory = { ctx ->
                        MapView(ctx).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            controller.setZoom(18.0)
                            val startPoint = GeoPoint(latitude, longitude)
                            controller.setCenter(startPoint)

                            // Add Marker for Student
                            val marker = Marker(this)
                            marker.position = startPoint
                            marker.title = studentName
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            overlays.add(marker)
                            
                            // Add Marker for Campus (Reference)
                             val campusPoint = GeoPoint(-7.811364, 110.320658) // UMY
                             val campusMarker = Marker(this)
                             campusMarker.position = campusPoint
                             campusMarker.title = "Kampus UMY"
                             campusMarker.icon = androidx.core.content.ContextCompat.getDrawable(ctx, org.osmdroid.library.R.drawable.person) // Fallback icon
                             // To differentiate, usually we need custom raw resource icons, but keeping simple for now
                             overlays.add(campusMarker)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Close Button
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.White.copy(alpha = 0.7f), androidx.compose.foundation.shape.CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close Map")
                }
            }
        }
    }
}
