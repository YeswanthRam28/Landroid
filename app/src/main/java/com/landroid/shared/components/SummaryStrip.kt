// app/src/main/java/com/landroid/shared/components/SummaryStrip.kt
package com.landroid.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.landroid.core.theme.LandroidColors
import com.landroid.core.theme.PlusJakartaSansFont

@Composable
fun SummaryStrip(
    parcelCount: Int,
    atRiskCount: Int,
    alertCount: Int
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .background(LandroidColors.PrimaryFixed, CircleShape)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Landscape,
                    contentDescription = null,
                    tint = LandroidColors.OnPrimaryFixedVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$parcelCount Parcels".uppercase(),
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = LandroidColors.OnPrimaryFixedVariant
                )
            }
        }
        item {
            Row(
                modifier = Modifier
                    .background(LandroidColors.TertiaryFixed, CircleShape)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = LandroidColors.OnTertiaryFixedVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$atRiskCount At Risk".uppercase(),
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = LandroidColors.OnTertiaryFixedVariant
                )
            }
        }
        item {
            Row(
                modifier = Modifier
                    .background(LandroidColors.ErrorContainer, CircleShape)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = null,
                    tint = LandroidColors.OnErrorContainer,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "$alertCount Alerts".uppercase(),
                    fontFamily = PlusJakartaSansFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = LandroidColors.OnErrorContainer
                )
            }
        }
    }
}
