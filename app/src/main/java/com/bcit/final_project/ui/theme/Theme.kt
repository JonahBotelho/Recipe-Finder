package com.bcit.final_project.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val PorcelainCobaltColorScheme = lightColorScheme(
    primary = Cobalt,
    onPrimary = Paper,
    primaryContainer = PaleBlue,
    onPrimaryContainer = Cobalt,
    secondary = Slate,
    onSecondary = Paper,
    secondaryContainer = PaleBlue,
    onSecondaryContainer = Ink,
    tertiary = Amber,
    onTertiary = Paper,
    background = Porcelain,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = Porcelain,
    onSurfaceVariant = Slate,
    surfaceTint = Cobalt,
    outline = Slate,
    outlineVariant = Mist,
    surfaceBright = Paper,
    surfaceDim = Mist,
    surfaceContainerLowest = Paper,
    surfaceContainerLow = Paper,
    surfaceContainer = Porcelain,
    surfaceContainerHigh = PaleBlue,
    surfaceContainerHighest = PaleBlue
)

@Composable
fun Final_projectTheme(content: @Composable () -> Unit) {
    // Keep the chosen light palette consistent with the Porcelain & Cobalt concept.
    MaterialTheme(
        colorScheme = PorcelainCobaltColorScheme,
        typography = Typography,
        shapes = Shapes(
            small = RoundedCornerShape(12.dp),
            medium = RoundedCornerShape(16.dp),
            large = RoundedCornerShape(20.dp)
        ),
        content = content
    )
}
