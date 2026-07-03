package me.alexy.hipipl.core.designsystem

import androidx.compose.ui.graphics.Color

// Legacy ad-hoc colors, still referenced by HostDetailsScreen (donation/rating/contact tint).
val LightGreen = Color(0xFFC4F9C6)
val Yellow = Color(0xFFFFFF00)
val LightPeach = Color(0xFFFFE4B5)
val Blue = Color(0xFF0077FF)

// Material3 palette hand-tuned from brand seed #FF6B35 (orange).
val md_theme_light_primary = Color(0xFFFF6B35)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFFFDBCB)
val md_theme_light_onPrimaryContainer = Color(0xFF3A0300)
val md_theme_light_secondary = Color(0xFF77574C)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFFFDBCB)
val md_theme_light_onSecondaryContainer = Color(0xFF2C160E)
val md_theme_light_tertiary = Color(0xFF2E6A4E)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFB5F1CD)
val md_theme_light_onTertiaryContainer = Color(0xFF00210F)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = Color(0xFFFFF8F6)
val md_theme_light_onBackground = Color(0xFF221813)
val md_theme_light_surface = Color(0xFFFFF8F6)
val md_theme_light_onSurface = Color(0xFF221813)
val md_theme_light_surfaceVariant = Color(0xFFF5DED4)
val md_theme_light_onSurfaceVariant = Color(0xFF53443C)
val md_theme_light_outline = Color(0xFF87736B)

/**
 * Fixed semantic accent for a host's gender, independent of light/dark theming
 * (per design brief: gray/blue/pink/purple must not shift with dynamic color).
 */
enum class GenderAccent { UNSPECIFIED, MALE, FEMALE, SEVERAL }

object GenderColors {
    val Unspecified = Color(0xFF9E9E9E)
    val Male = Color(0xFF2196F3)
    val Female = Color(0xFFE91E8C)
    val Several = Color(0xFF9C27B0)
}

fun GenderAccent.toColor(): Color = when (this) {
    GenderAccent.UNSPECIFIED -> GenderColors.Unspecified
    GenderAccent.MALE -> GenderColors.Male
    GenderAccent.FEMALE -> GenderColors.Female
    GenderAccent.SEVERAL -> GenderColors.Several
}
