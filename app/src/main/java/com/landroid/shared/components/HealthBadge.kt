// app/src/main/java/com/landroid/shared/components/HealthBadge.kt
package com.landroid.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.landroid.core.theme.LandroidColors
import com.landroid.core.theme.LandroidShapes
import com.landroid.core.theme.PlusJakartaSansFont
import com.landroid.shared.models.HealthStatus

@Composable
fun HealthBadge(status: HealthStatus) {
    val (bg, text, label) = when (status) {
        HealthStatus.HEALTHY -> Triple(LandroidColors.PrimaryFixed, LandroidColors.OnPrimaryFixedVariant, "Healthy")
        HealthStatus.MODERATE -> Triple(LandroidColors.TertiaryFixed, LandroidColors.OnTertiaryFixedVariant, "Moderate")
        HealthStatus.AT_RISK -> Triple(LandroidColors.ErrorContainer, LandroidColors.OnErrorContainer, "At Risk")
    }

    Box(
        modifier = Modifier
            .background(bg, LandroidShapes.Badge)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label.uppercase(),
            fontFamily = PlusJakartaSansFont,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color = text,
            letterSpacing = 0.05.sp
        )
    }
}
