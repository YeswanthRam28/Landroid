// app/src/main/java/com/landroid/features/valuation/presentation/ValuationScreen.kt
package com.landroid.features.valuation.presentation

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.landroid.core.theme.LandroidColors
import com.landroid.core.theme.LandroidShapes
import com.landroid.core.theme.NewsreaderFont
import com.landroid.core.theme.PlusJakartaSansFont
import com.landroid.shared.components.ConfidenceBar
import com.landroid.shared.models.ValuationFactor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValuationScreen(
    parcelId: String,
    navController: NavController,
    viewModel: ValuationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(parcelId) { viewModel.load(parcelId) }

    Scaffold(
        containerColor = LandroidColors.Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Estimated Land Value",
                        fontFamily = NewsreaderFont,
                        fontSize = 22.sp,
                        color = LandroidColors.OnSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back", tint = LandroidColors.PrimaryContainer)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LandroidColors.Surface)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Disclaimer chip
            item {
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            "Intelligence estimate — not a legal valuation",
                            fontFamily = PlusJakartaSansFont,
                            fontSize = 11.sp
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Outlined.Info, null, modifier = androidx.compose.ui.Modifier.padding(end = 4.dp))
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = LandroidColors.TertiaryFixed,
                        labelColor = LandroidColors.OnTertiaryFixedVariant,
                        leadingIconContentColor = LandroidColors.OnTertiaryFixedVariant
                    )
                )
            }

            // Valuation bands
            itemsIndexed(state.bands) { index, band ->
                val bandHeight = if (band.isFeatured) 60.dp else 48.dp
                val bg = if (band.isFeatured) LandroidColors.PrimaryFixed else LandroidColors.SurfaceContainerLow
                val border = if (band.isFeatured) BorderStroke(2.dp, LandroidColors.AccentAmber) else null

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(bandHeight)
                        .then(
                            if (border != null)
                                Modifier.clip(RoundedCornerShape(8.dp))
                                    .background(bg)
                                    .then(Modifier) // border handled via Card below
                            else Modifier.clip(RoundedCornerShape(8.dp)).background(bg)
                        )
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = band.label,
                            fontFamily = PlusJakartaSansFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = LandroidColors.OnSurface
                        )
                        Text(
                            text = "${band.lowValue} – ${band.highValue}",
                            fontFamily = NewsreaderFont,
                            fontWeight = FontWeight.Bold,
                            fontStyle = if (band.isFeatured) FontStyle.Italic else FontStyle.Normal,
                            fontSize = if (band.isFeatured) 20.sp else 16.sp,
                            color = if (band.isFeatured) LandroidColors.PrimaryContainer else LandroidColors.OnSurface
                        )
                    }
                }
            }

            // Confidence
            item { ConfidenceBar(confidence = state.confidence) }

            // Factors header
            item {
                Text(
                    text = "What's driving this estimate",
                    fontFamily = NewsreaderFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = LandroidColors.OnSurface
                )
            }

            // Factor rows
            itemsIndexed(state.factors) { index, factor ->
                FactorRow(rank = index + 1, factor = factor)
            }

            // Info chip
            item {
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            "Updates when Land Health Score changes",
                            fontFamily = PlusJakartaSansFont,
                            fontSize = 11.sp
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(containerColor = LandroidColors.SurfaceContainer)
                )
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun FactorRow(rank: Int, factor: ValuationFactor) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "$rank",
            fontFamily = NewsreaderFont,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = LandroidColors.PrimaryContainer,
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.Center
        )
        Text(
            text = factor.name,
            fontFamily = PlusJakartaSansFont,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = LandroidColors.OnSurface,
            modifier = Modifier.weight(1f)
        )
        AssistChip(
            onClick = {},
            label = {
                Text(
                    if (factor.isPositive) "↑ Positive" else "↓ Negative",
                    fontFamily = PlusJakartaSansFont,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            leadingIcon = {
                Icon(
                    if (factor.isPositive) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
                    null,
                    modifier = Modifier.padding(end = 2.dp)
                )
            },
            colors = AssistChipDefaults.assistChipColors(
                containerColor = if (factor.isPositive) LandroidColors.PrimaryFixed else LandroidColors.ErrorContainer,
                labelColor = if (factor.isPositive) LandroidColors.OnPrimaryFixedVariant else LandroidColors.OnErrorContainer,
                leadingIconContentColor = if (factor.isPositive) LandroidColors.OnPrimaryFixedVariant else LandroidColors.OnErrorContainer
            )
        )
        Text(
            text = "${(factor.weight * 100).toInt()}%",
            fontFamily = PlusJakartaSansFont,
            fontSize = 12.sp,
            color = LandroidColors.Outline
        )
    }
}
