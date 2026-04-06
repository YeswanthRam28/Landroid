// app/src/main/java/com/landroid/features/parcels/presentation/ParcelCreateScreen.kt
package com.landroid.features.parcels.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.landroid.core.theme.LandroidColors
import com.landroid.core.theme.LandroidShapes
import com.landroid.core.theme.NewsreaderFont
import com.landroid.core.theme.PlusJakartaSansFont
import com.landroid.shared.models.HealthStatus
import com.landroid.shared.models.Parcel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParcelCreateScreen(
    navController: NavController,
    viewModel: ParcelViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var assignedTo by remember { mutableStateOf("") }
    var soilType by remember { mutableStateOf("") }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = LandroidColors.PrimaryContainer,
        unfocusedBorderColor = LandroidColors.OutlineVariant,
        focusedLabelColor = LandroidColors.PrimaryContainer,
        cursorColor = LandroidColors.PrimaryContainer,
        focusedContainerColor = LandroidColors.SurfaceContainerLowest,
        unfocusedContainerColor = LandroidColors.SurfaceContainerLow
    )

    Scaffold(
        containerColor = LandroidColors.Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "New Parcel",
                        fontFamily = NewsreaderFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp,
                        color = LandroidColors.OnSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back",
                            tint = LandroidColors.PrimaryContainer
                        )
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Parcel Name", fontFamily = PlusJakartaSansFont) },
                modifier = Modifier.fillMaxWidth(), shape = LandroidShapes.Button, colors = fieldColors
            )
            OutlinedTextField(
                value = location, onValueChange = { location = it },
                label = { Text("Location / Village", fontFamily = PlusJakartaSansFont) },
                modifier = Modifier.fillMaxWidth(), shape = LandroidShapes.Button, colors = fieldColors
            )
            OutlinedTextField(
                value = district, onValueChange = { district = it },
                label = { Text("District", fontFamily = PlusJakartaSansFont) },
                modifier = Modifier.fillMaxWidth(), shape = LandroidShapes.Button, colors = fieldColors
            )
            OutlinedTextField(
                value = area, onValueChange = { area = it },
                label = { Text("Area (acres)", fontFamily = PlusJakartaSansFont) },
                modifier = Modifier.fillMaxWidth(), shape = LandroidShapes.Button, colors = fieldColors
            )
            OutlinedTextField(
                value = soilType, onValueChange = { soilType = it },
                label = { Text("Soil Type", fontFamily = PlusJakartaSansFont) },
                modifier = Modifier.fillMaxWidth(), shape = LandroidShapes.Button, colors = fieldColors
            )
            OutlinedTextField(
                value = assignedTo, onValueChange = { assignedTo = it },
                label = { Text("Assigned To", fontFamily = PlusJakartaSansFont) },
                modifier = Modifier.fillMaxWidth(), shape = LandroidShapes.Button, colors = fieldColors
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    val parcel = Parcel(
                        id = java.util.UUID.randomUUID().toString(),
                        name = name, location = location, district = district,
                        areaAcres = area.toDoubleOrNull() ?: 0.0,
                        healthScore = 50, healthStatus = HealthStatus.MODERATE,
                        ndvi = 0.0, rainfall = 0.0,
                        soilType = soilType, assignedTo = assignedTo,
                        boundaryGeoJson = "", centroidLat = 0.0, centroidLng = 0.0,
                        createdAt = System.currentTimeMillis()
                    )
                    viewModel.createParcel(parcel)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LandroidColors.PrimaryContainer,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Create Parcel",
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
