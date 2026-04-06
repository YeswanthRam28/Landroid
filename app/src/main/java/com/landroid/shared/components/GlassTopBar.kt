// app/src/main/java/com/landroid/shared/components/GlassTopBar.kt
package com.landroid.shared.components

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.landroid.core.theme.LandroidColors
import com.landroid.core.theme.NewsreaderFont

@Composable
fun GlassTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: ImageVector = Icons.Outlined.Share,
    onTrailingClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.85f))
            .graphicsLayer {
                shadowElevation = 8f
            }
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back",
                tint = LandroidColors.OnSurface
            )
        }

        Text(
            text = title,
            fontFamily = NewsreaderFont,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Normal,
            fontSize = 20.sp,
            color = LandroidColors.OnSurface,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        IconButton(
            onClick = onTrailingClick,
            modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)
        ) {
            Icon(
                imageVector = trailingIcon,
                contentDescription = "Action",
                tint = LandroidColors.OnSurface
            )
        }
    }
}
