package com.johnvazna.focusquest.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * The source design pairs Spectral for display text with Space Grotesk for everything else.
 * Neither font is bundled yet, so the platform serif and sans-serif families stand in for them.
 * The scale, weights, line heights, and tracking below are the design's own values.
 */
private val DisplayFamily = FontFamily.Serif
private val TextFamily = FontFamily.SansSerif

internal val FocusQuestTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = DisplayFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 34.sp,
        lineHeight = 37.4.sp,
        letterSpacing = (-0.34).sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = DisplayFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 26.sp,
        lineHeight = 33.8.sp,
        letterSpacing = (-0.31).sp,
    ),
    titleMedium = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.16).sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.Light,
        fontSize = 13.5.sp,
        lineHeight = 25.7.sp,
        letterSpacing = 0.07.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = (-0.15).sp,
    ),
    labelSmall = TextStyle(
        fontFamily = TextFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.5.sp,
        lineHeight = 14.sp,
    ),
)
