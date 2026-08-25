package com.johnvazna.focusquest.feature.timeline.impl.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.johnvazna.focusquest.core.designsystem.component.FocusQuestEmptyProjectLayout
import com.johnvazna.focusquest.core.designsystem.component.FocusQuestProgressFieldState
import com.johnvazna.focusquest.core.designsystem.component.rememberFocusQuestProgressFieldState
import com.johnvazna.focusquest.core.designsystem.theme.FocusQuestTheme
import com.johnvazna.focusquest.feature.timeline.impl.R

@Composable
fun TimelineRoute(
    onCreateProject: () -> Unit,
    modifier: Modifier = Modifier,
    progressFieldState: FocusQuestProgressFieldState = rememberFocusQuestProgressFieldState(),
) {
    TimelineScreen(
        onCreateProject = onCreateProject,
        modifier = modifier,
        progressFieldState = progressFieldState,
    )
}

@Composable
fun TimelineScreen(
    onCreateProject: () -> Unit,
    modifier: Modifier = Modifier,
    progressFieldState: FocusQuestProgressFieldState = rememberFocusQuestProgressFieldState(),
) {
    FocusQuestEmptyProjectLayout(
        bannerTitle = stringResource(R.string.timeline_title),
        headline = stringResource(R.string.timeline_empty_headline),
        body = stringResource(R.string.timeline_empty_body),
        actionLabel = stringResource(R.string.timeline_create_project),
        onAction = onCreateProject,
        modifier = modifier,
        progressFieldState = progressFieldState,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFAF7F1, widthDp = 402, heightDp = 874)
@Composable
private fun TimelineEmptyPreview() {
    FocusQuestTheme {
        TimelineScreen(onCreateProject = {})
    }
}
