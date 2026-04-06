// app/src/main/java/com/landroid/core/theme/Theme.kt
package com.landroid.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

private val LandroidColorScheme = lightColorScheme(
    primary = LandroidColors.Primary,
    onPrimary = LandroidColors.OnPrimary,
    primaryContainer = LandroidColors.PrimaryContainer,
    onPrimaryContainer = LandroidColors.PrimaryFixedDim,
    secondary = LandroidColors.Secondary,
    onSecondary = LandroidColors.OnSecondary,
    secondaryContainer = LandroidColors.SecondaryContainer,
    tertiary = LandroidColors.Tertiary,
    onTertiary = LandroidColors.OnTertiary,
    tertiaryContainer = LandroidColors.TertiaryContainer,
    onTertiaryContainer = LandroidColors.OnTertiaryContainer,
    error = LandroidColors.Error,
    onError = LandroidColors.OnError,
    errorContainer = LandroidColors.ErrorContainer,
    onErrorContainer = LandroidColors.OnErrorContainer,
    background = LandroidColors.Surface,
    onBackground = LandroidColors.OnSurface,
    surface = LandroidColors.Surface,
    onSurface = LandroidColors.OnSurface,
    onSurfaceVariant = LandroidColors.OnSurfaceVariant,
    outline = LandroidColors.Outline,
    outlineVariant = LandroidColors.OutlineVariant,
    inverseSurface = LandroidColors.InverseSurface,
    inverseOnSurface = LandroidColors.InverseOnSurface,
    surfaceVariant = LandroidColors.SurfaceContainerHighest,
    surfaceTint = LandroidColors.PrimaryContainer
)

private val LandroidShapeScheme = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = LandroidShapes.Badge,
    medium = LandroidShapes.Button,
    large = LandroidShapes.Card,
    extraLarge = LandroidShapes.BottomDrawer
)

@Composable
fun LandroidTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LandroidColorScheme,
        typography = LandroidTypography,
        shapes = LandroidShapeScheme,
        content = content
    )
}
