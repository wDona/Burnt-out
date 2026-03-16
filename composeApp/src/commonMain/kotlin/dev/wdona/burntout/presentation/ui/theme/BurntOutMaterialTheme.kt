package dev.wdona.burntout.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


// 0x A R G B --- #AA3388
private val lightPrimary = Color(0xFF8c156e)
private val lightSecondary = Color(0xFF755166)
private val lightTertiary = Color(0xFF8b4f2f)
private val lightError = Color(0xFFba1a1a)

private val lightOnPrimary = Color(0xFFffffff)
private val lightOnSecondary = Color(0xFFffffff)
private val lightOnTertiary = Color(0xFFffffff)
private val lightOnError = Color(0xFFffffff)
private val lightErrorContainer = Color(0xFFffdad6)
private val lightOnErrorContainer = Color(0xFF93000a)

private val lightPrimaryContainer = Color(0xFFaa3388)
private val lightSecondaryContainer = Color(0xFF8f6a7f)
private val lightTertiaryContainer = Color(0xFFc6805c)

private val lightOnPrimaryContainer = Color(0xFFffd6eb)
private val lightOnSecondaryContainer = Color(0xFFfffbff)
private val lightOnTertiaryContainer = Color(0xFF4a1c02)

private val lightInversePrimary = Color(0xFFffaede)
private val lightInverseSurface = Color(0xFF313030)
private val lightInverseOnSurface = Color(0xFFf4f0ef)

private val lightOutlineVariant = Color(0xFFcfc3c7)
private val lightOutline = Color(0xFF7e7578)
private val lightOnSurfaceVariant = Color(0xFF4c4548)
private val lightOnSurface = Color(0xFF1c1b1b)

private val lightSurface = Color(0xFFfdf8f8)
private val lightSurfaceDim = Color(0xFFddd9d9)
private val lightSurfaceBright = Color(0xFFfdf8f8)
private val lightSurfaceContainerLowest = Color(0xFFffffff)
private val lightSurfaceContainerLow = Color(0xFFf7f2f2)
private val lightSurfaceContainer = Color(0xFFf1edec)
private val lightSurfaceContainerHigh = Color(0xFFece7e7)
private val lightSurfaceContainerHighest = Color(0xFFe6e1e1)

// Paleta para Modo Claro
val LightColorScheme = lightColorScheme(
    primary = lightPrimary,
    secondary = lightSecondary,
    tertiary = lightTertiary,
    background = lightSurface,
    surface = lightSurface,
    onBackground = lightOnSurface,
    onPrimary = lightOnPrimary,
    onSecondary = lightOnSecondary,
    onTertiary = lightOnTertiary,
    primaryContainer = lightPrimaryContainer,
    secondaryContainer = lightSecondaryContainer,
    tertiaryContainer = lightTertiaryContainer,
    onPrimaryContainer = lightOnPrimaryContainer,
    onSecondaryContainer = lightOnSecondaryContainer,
    onTertiaryContainer = lightOnTertiaryContainer,
    inversePrimary = lightInversePrimary,
    inverseSurface = lightInverseSurface,
    inverseOnSurface = lightInverseOnSurface,
    outlineVariant = lightOutlineVariant,
    outline = lightOutline,
    onSurfaceVariant = lightOnSurfaceVariant,
    onSurface = lightOnSurface,
    surfaceBright = lightSurfaceBright,
    surfaceDim = lightSurfaceDim,
    surfaceContainer = lightSurfaceContainer,
    surfaceContainerHigh = lightSurfaceContainerHigh,
    surfaceContainerHighest = lightSurfaceContainerHighest,
    surfaceContainerLow = lightSurfaceContainerLow,
    surfaceContainerLowest = lightSurfaceContainerLowest,
    error = lightError,
    onError = lightOnError,
    errorContainer = lightErrorContainer,
    onErrorContainer = lightOnErrorContainer

)

private val darkPrimary = Color(0xFFffaede)
private val darkSecondary = Color(0xFFe6bad2)
private val darkTertiary = Color(0xFFffb691)
private val darkError = Color(0xFFffb4ab)
private val darkOnPrimary = Color(0xFF60004b)
private val darkOnSecondary = Color(0xFF452739)
private val darkOnTertiary = Color(0xFF522306)
private val darkOnError = Color(0xFF690005)
private val darkErrorContainer = Color(0xFF93000a)
private val darkOnErrorContainer = Color(0xFFffdad6)
private val darkPrimaryContainer = Color(0xFFaa3388)
private val darkSecondaryContainer = Color(0xFFad859b)
private val darkTertiaryContainer = Color(0xFFc6805c)
private val darkOnPrimaryContainer = Color(0xFFffd6eb)
private val darkOnSecondaryContainer = Color(0xFF3d2032)
private val darkOnTertiaryContainer = Color(0xFF4a1c02)
private val darkInversePrimary = Color(0xFFa32d82)
private val darkInverseSurface = Color(0xFFe6e1e1)
private val darkInverseOnSurface = Color(0xFF313030)
private val darkOutline = Color(0xFF988e92)
private val darkOutlineVariant = Color(0xFF4c4548)
private val darkOnSurfaceVariant = Color(0xFFcfc3c7)
private val darkOnSurface = Color(0xFFe6e1e1)
private val darkSurface = Color(0xFF141313)
private val darkSurfaceDim = Color(0xFF141313)
private val darkSurfaceBright = Color(0xFF3a3939)
private val darkSurfaceContainerLowest = Color(0xFF0f0e0e)
private val darkSurfaceContainerLow = Color(0xFF1c1b1b)
private val darkSurfaceContainer = Color(0xFF201f1f)
private val darkSurfaceContainerHigh = Color(0xFF2b2a2a)
private val darkSurfaceContainerHighest = Color(0xFF363434)


// Paleta para Modo Oscuro
val DarkColorScheme = darkColorScheme(
    primary = darkPrimary,
    onPrimary = darkOnPrimary,
    primaryContainer = darkPrimaryContainer,
    onPrimaryContainer = darkOnPrimaryContainer,
    inversePrimary = darkInversePrimary,
    secondary = darkSecondary,
    onSecondary = darkOnSecondary,
    secondaryContainer = darkSecondaryContainer,
    onSecondaryContainer = darkOnSecondaryContainer,
    tertiary = darkTertiary,
    onTertiary = darkOnTertiary,
    tertiaryContainer = darkTertiaryContainer,
    onTertiaryContainer = darkOnTertiaryContainer,
    background = darkSurface,
    onBackground = darkOnSurface,
    surface = darkSurface,
    onSurface = darkOnSurface,
    surfaceVariant = darkSurfaceContainer,
    onSurfaceVariant = darkOnSurfaceVariant,
    surfaceTint = darkPrimary,
    inverseSurface = darkInverseSurface,
    inverseOnSurface = darkInverseOnSurface,
    error = darkError,
    onError = darkOnError,
    errorContainer = darkErrorContainer,
    onErrorContainer = darkOnErrorContainer,
    outline = darkOutline,
    outlineVariant = darkOutlineVariant,
    scrim = Color(0xFF000000),
    surfaceBright = darkSurfaceBright,
    surfaceDim = darkSurfaceDim,
    surfaceContainer = darkSurfaceContainer,
    surfaceContainerHigh = darkSurfaceContainerHigh,
    surfaceContainerHighest = darkSurfaceContainerHighest,
    surfaceContainerLow = darkSurfaceContainerLow,
    surfaceContainerLowest = darkSurfaceContainerLowest
)

@Composable
fun BurntOutMaterialTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = BurntOutMaterialTheme.getColorScheme(isDarkTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

class BurntOutMaterialTheme() {
    companion object {
        @Composable
        fun getColorScheme(darkTheme: Boolean = isSystemInDarkTheme()) = if (darkTheme) DarkColorScheme else LightColorScheme

        @Composable
        fun getSuccessColor() = if (isSystemInDarkTheme()) Color(0xFF81C784) else Color(0xFF2E7D32)

        @Composable
        fun getWarningColor() = if (isSystemInDarkTheme()) Color(0xFFFFB871) else Color(0xFFA25000)


    }
}


