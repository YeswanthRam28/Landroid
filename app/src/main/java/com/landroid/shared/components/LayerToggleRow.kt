// app/src/main/java/com/landroid/shared/components/LayerToggleRow.kt
package com.landroid.shared.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.landroid.core.theme.LandroidColors
import com.landroid.core.theme.PlusJakartaSansFont

@Composable
fun LayerToggleRow(
    layers: List<String>,
    activeLayer: String,
    onLayerSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
    ) {
        items(layers) { layer ->
            val isActive = layer == activeLayer
            Text(
                text = layer,
                fontFamily = PlusJakartaSansFont,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp,
                color = if (isActive) Color.White else LandroidColors.Secondary,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        if (isActive) LandroidColors.PrimaryContainer
                        else LandroidColors.SurfaceContainerLowest.copy(alpha = 0.9f)
                    )
                    .then(
                        if (!isActive) Modifier.border(
                            width = 1.dp,
                            color = LandroidColors.OutlineVariant,
                            shape = CircleShape
                        ) else Modifier
                    )
                    .clickable { onLayerSelect(layer) }
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }
    }
}
