// app/src/main/java/com/landroid/features/documents/presentation/DocumentVaultScreen.kt
package com.landroid.features.documents.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.landroid.core.theme.LandroidColors
import com.landroid.core.theme.LandroidShapes
import com.landroid.core.theme.NewsreaderFont
import com.landroid.core.theme.PlusJakartaSansFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentVaultScreen(
    parcelId: String,
    navController: NavController,
    viewModel: DocumentViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(parcelId) { viewModel.loadDocuments(parcelId) }

    Scaffold(
        containerColor = LandroidColors.Surface,
        topBar = {
            TopAppBar(
                title = { Text("Document Vault", fontFamily = NewsreaderFont, fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back", tint = LandroidColors.PrimaryContainer)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LandroidColors.Surface)
            )
        },
        floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(
                onClick = { navController.navigate("consultant_upload/$parcelId") },
                containerColor = LandroidColors.PrimaryContainer,
                contentColor = androidx.compose.ui.graphics.Color.White
            ) {
                Icon(Icons.Outlined.CloudUpload, "Upload Data")
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LandroidColors.PrimaryContainer)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.documents, key = { it.name }) { doc ->
                    DocumentRow(doc = doc)
                }
                item { Spacer(Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
private fun DocumentRow(doc: DocumentItem) {
    val (icon, tint) = when (doc.type) {
        DocumentType.PDF     -> Icons.Outlined.Description to LandroidColors.Error
        DocumentType.IMAGE   -> Icons.Outlined.Image to LandroidColors.Secondary
        DocumentType.GEOTIFF -> Icons.Outlined.Map to LandroidColors.PrimaryContainer
        DocumentType.OTHER   -> Icons.Outlined.Description to LandroidColors.Outline
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(LandroidShapes.Card)
            .background(LandroidColors.SurfaceContainerLowest)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp))
                .background(tint.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(doc.name, fontFamily = PlusJakartaSansFont, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = LandroidColors.OnSurface)
            Text("${doc.sizeKb} KB", fontFamily = PlusJakartaSansFont, fontSize = 12.sp, color = LandroidColors.Outline)
        }
        IconButton(onClick = { /* open downloadUrl */ }) {
            Icon(Icons.Outlined.FileDownload, "Download", tint = LandroidColors.PrimaryContainer)
        }
    }
}
