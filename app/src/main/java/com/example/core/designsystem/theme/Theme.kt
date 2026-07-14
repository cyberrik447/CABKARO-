package com.example.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.example.core.designsystem.theme.LocalSpacing
import com.example.core.designsystem.theme.Spacing

/**
 * Premium Soft Light Color Scheme (Default)
 */
private val SoftLightColorScheme = lightColorScheme(
    primary = PremiumSlate,
    onPrimary = IvoryWhite,
    secondary = MutedSlate,
    onSecondary = CharcoalDark,
    tertiary = WarmGold,
    background = IvoryWhite,
    onBackground = CharcoalDark,
    surface = PlatinumGray,
    onSurface = CharcoalDark,
    error = ErrorRed
)

/**
 * Pure Black Color Scheme (Future Support)
 */
private val PureBlackColorScheme = darkColorScheme(
    primary = PureWhite,
    onPrimary = PureBlack,
    secondary = MutedSlate,
    onSecondary = PureWhite,
    tertiary = WarmGold,
    background = PureBlack,
    onBackground = PureWhite,
    surface = CharcoalDark,
    onSurface = PureWhite,
    error = ErrorRed
)

/**
 * Pure White Color Scheme (Future Support)
 */
private val PureWhiteColorScheme = lightColorScheme(
    primary = PureBlack,
    onPrimary = PureWhite,
    secondary = MutedSlate,
    onSecondary = PureBlack,
    tertiary = WarmGold,
    background = PureWhite,
    onBackground = PureBlack,
    surface = PlatinumGray,
    onSurface = PureBlack,
    error = ErrorRed
)

/**
 * Enum defining available custom themes for Cabkaro.
 */
enum class CabkaroThemeMode {
    SOFT_PREMIUM,
    PURE_WHITE,
    PURE_BLACK
}

@Composable
fun CabkaroTheme(
    themeMode: CabkaroThemeMode = CabkaroThemeMode.SOFT_PREMIUM,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        CabkaroThemeMode.SOFT_PREMIUM -> SoftLightColorScheme
        CabkaroThemeMode.PURE_WHITE -> PureWhiteColorScheme
        CabkaroThemeMode.PURE_BLACK -> PureBlackColorScheme
    }

    CompositionLocalProvider(
        LocalSpacing provides Spacing()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}
