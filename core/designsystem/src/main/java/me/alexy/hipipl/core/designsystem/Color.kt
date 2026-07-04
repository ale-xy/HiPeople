package me.alexy.hipipl.core.designsystem

import androidx.compose.ui.graphics.Color

// Legacy ad-hoc colors, still referenced by HostDetailsScreen (donation/rating/contact tint).
val LightGreen = Color(0xFFC4F9C6)
val Yellow = Color(0xFFFFFF00)
val LightPeach = Color(0xFFFFE4B5)
val Blue = Color(0xFF0077FF)

// Material3 palette per the "HiPeople — Material 3 Color Theme" spec (material-color-theme.md).
val md_theme_light_primary = Color(0xFFC13D14)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFFFDBCC)
val md_theme_light_onPrimaryContainer = Color(0xFF3B0A00)
val md_theme_light_secondary = Color(0xFF77574C)
val md_theme_light_secondaryContainer = Color(0xFFF7DED4)
val md_theme_light_onSecondaryContainer = Color(0xFF2C160E)
val md_theme_light_surface = Color(0xFFFFF8F6)
val md_theme_light_surfaceContainerLow = Color(0xFFFFF1EB)
val md_theme_light_surfaceContainer = Color(0xFFFBEBE3)
val md_theme_light_surfaceContainerHigh = Color(0xFFF5E5DD)
val md_theme_light_surfaceContainerHighest = Color(0xFFEFDFD7)
val md_theme_light_onSurface = Color(0xFF221813)
val md_theme_light_onSurfaceVariant = Color(0xFF54433C)
val md_theme_light_outline = Color(0xFF87736B)
val md_theme_light_outlineVariant = Color(0xFFDBC4BB)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_inverseSurface = Color(0xFF322A26)
val md_theme_light_inverseOnSurface = Color(0xFFF4ECE8)

val md_theme_light_background = md_theme_light_surface
val md_theme_light_onBackground = md_theme_light_onSurface

/** Custom semantic tokens from the spec that sit outside strict M3 role names. */
object AppColors {
    val Divider = Color(0xFFEAD9D0)
    val Success = Color(0xFF3B6939)
    val SuccessContainer = Color(0xFFDFF3E6)
    val Warning = Color(0xFFF2A93B)
    val InfoCta = Color(0xFF4F7CF7)
    val RatingAccent = Color(0xFFFF6B35)
    val Scrim = Color(0xFF281409)
    val OnSurfaceBodyCopy = Color(0xFF3A2E28)
}

/**
 * Fixed semantic accent for a host's gender, independent of light/dark theming
 * (per design brief: gray/blue/pink/purple must not shift with dynamic color).
 */
enum class GenderAccent { UNSPECIFIED, MALE, FEMALE, SEVERAL }

object GenderColors {
    val Unspecified = Color(0xFF9AA0A6)
    val Male = Color(0xFF4F7CF7)
    val Female = Color(0xFFE0589F)
    val Several = Color(0xFF8A63D2)
}

fun GenderAccent.toColor(): Color = when (this) {
    GenderAccent.UNSPECIFIED -> GenderColors.Unspecified
    GenderAccent.MALE -> GenderColors.Male
    GenderAccent.FEMALE -> GenderColors.Female
    GenderAccent.SEVERAL -> GenderColors.Several
}
