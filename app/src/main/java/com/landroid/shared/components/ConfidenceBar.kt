// app/src/main/java/com/landroid/shared/components/ConfidenceBar.kt
package com.landroid.shared.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.landroid.core.theme.LandroidColors
import com.landroid.core.theme.PlusJakartaSansFont

@Composable
fun ConfidenceBar(
    confidence: Int,
    caption: String = "VERIFIED VIA SENTINEL-2 SATELLITE INTELLIGENCE",
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "CONFIDENCE LEVEL",
                fontFamily = PlusJakartaSansFont,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = LandroidColors.Outline,
                letterSpacing = 0.1.em,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$confidence%",
                fontFamily = PlusJakartaSansFont,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = LandroidColors.OnSurface
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = { confidence / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = LandroidColors.PrimaryContainer,
            trackColor = LandroidColors.SurfaceContainerHigh
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = caption,
            fontFamily = PlusJakartaSansFont,
            fontSize = 10.sp,
            color = LandroidColors.Outline,
            letterSpacing = 0.1.em,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
