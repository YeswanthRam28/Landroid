// app/src/main/java/com/landroid/shared/components/SearchBar.kt
package com.landroid.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.landroid.core.theme.LandroidColors
import com.landroid.core.theme.PlusJakartaSansFont

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search parcels…",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(Color(0xFFEDEAE3), CircleShape)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search",
                tint = LandroidColors.Outline,
                modifier = Modifier.size(20.dp)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                if (query.isEmpty()) {
                    Text(
                        text = placeholder,
                        fontFamily = PlusJakartaSansFont,
                        fontSize = 14.sp,
                        color = LandroidColors.OnSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    textStyle = TextStyle(
                        fontFamily = PlusJakartaSansFont,
                        fontSize = 14.sp,
                        color = LandroidColors.OnSurface
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Icon(
                imageVector = Icons.Outlined.Tune,
                contentDescription = "Filter",
                tint = LandroidColors.Outline,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
