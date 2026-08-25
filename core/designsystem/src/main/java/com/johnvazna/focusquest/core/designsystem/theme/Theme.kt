package com.johnvazna.focusquest.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * FocusQuest renders a single light treatment.
 *
 * The source design defines no dark treatment and no dynamic-colour behaviour, so neither is
 * exposed here. Inventing either would put colours in the product that the design does not
 * describe. A dark treatment becomes a theme parameter when the design defines one.
 */
private val FocusQuestColorScheme = lightColorScheme(
    primary = FocusQuestInk,
    onPrimary = FocusQuestPaper,
    secondary = FocusQuestInk,
    onSecondary = FocusQuestPaper,
    background = FocusQuestPaper,
    onBackground = FocusQuestInk,
    surface = FocusQuestPaper,
    onSurface = FocusQuestInk,
    surfaceContainerLowest = FocusQuestPaper,
    surfaceContainerLow = FocusQuestPaper,
    surfaceContainer = FocusQuestPaper,
    surfaceContainerHigh = FocusQuestPaper,
    surfaceContainerHighest = FocusQuestPaper,
    outline = FocusQuestInk.copy(alpha = 0.28f),
    outlineVariant = FocusQuestInk.copy(alpha = 0.08f),
)

@Composable
fun FocusQuestTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = FocusQuestColorScheme,
        typography = FocusQuestTypography,
        content = content,
    )
}
