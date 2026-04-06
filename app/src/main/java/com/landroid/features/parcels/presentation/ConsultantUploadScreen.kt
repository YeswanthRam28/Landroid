// app/src/main/java/com/landroid/features/parcels/presentation/ConsultantUploadScreen.kt
package com.landroid.features.parcels.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.EditLocation
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.landroid.core.theme.LandroidColors
import com.landroid.core.theme.NewsreaderFont
import com.landroid.core.theme.PlusJakartaSansFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultantUploadScreen(
    parcelId: String,
    navController: NavController
) {
    var isUploading by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        // In production: Multipart Push to `/api/documents/upload`
        if (uri != null) {
            isUploading = true
            // Simulate Upload
        }
    }

    Scaffold(
        containerColor = LandroidColors.Surface,
        topBar = {
            TopAppBar(
                title = { Text("Consultant Terminal", fontFamily = NewsreaderFont, fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back", tint = LandroidColors.PrimaryContainer)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LandroidColors.Surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Birdscale Drone Imagery Pipeline",
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = LandroidColors.PrimaryContainer
            )
            
            Text(
                "Upload high-resolution multispectral data directly from your UAV survey. The Landroid backend will geometrically bind these to the MapLibre engine.",
                fontFamily = PlusJakartaSansFont,
                fontSize = 14.sp,
                color = LandroidColors.OnSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            UploadCard(
                title = "Orthomosaic (.tif)",
                desc = "Primary true-color drone map used for Canopy OpenCV detection",
                onClick = { filePickerLauncher.launch("image/tiff") }
            )

            UploadCard(
                title = "Digital Elevation Model (DEM)",
                desc = "Terrain mapping data for flow accumulation scaling",
                onClick = { filePickerLauncher.launch("image/tiff") }
            )
            
            Spacer(Modifier.height(32.dp))

            Text(
                "Georeferencing & Boundaries",
                fontFamily = PlusJakartaSansFont,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = LandroidColors.PrimaryContainer
            )

            Card(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = LandroidColors.SurfaceContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.EditLocation, null, tint = LandroidColors.PrimaryContainer, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Draw Boundary Polygon", fontFamily = PlusJakartaSansFont, fontWeight = FontWeight.Bold)
                        Text("Manually define vertices in Map View", fontFamily = PlusJakartaSansFont, fontSize = 12.sp, color = LandroidColors.Outline)
                    }
                    Button(onClick = { /* Navigate to Map Draw UI Fragment */ }) {
                        Text("Draw")
                    }
                }
            }
        }
    }
}

@Composable
fun UploadCard(title: String, desc: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = LandroidColors.SurfaceContainerHighest)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Map, null, tint = LandroidColors.Secondary, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontFamily = PlusJakartaSansFont, fontWeight = FontWeight.Bold)
                Text(desc, fontFamily = PlusJakartaSansFont, fontSize = 12.sp, color = LandroidColors.Outline)
            }
            IconButton(onClick = onClick) {
                Icon(Icons.Outlined.CloudUpload, null, tint = LandroidColors.PrimaryContainer)
            }
        }
    }
}
