package com.johnvazna.focusquest.feature.project.impl.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.johnvazna.focusquest.core.designsystem.component.FocusQuestEmptyProjectLayout
import com.johnvazna.focusquest.core.designsystem.component.FocusQuestProgressFieldState
import com.johnvazna.focusquest.core.designsystem.component.rememberFocusQuestProgressFieldState
import com.johnvazna.focusquest.core.designsystem.theme.FocusQuestTheme
import com.johnvazna.focusquest.feature.project.impl.R

@Composable
fun ProjectRoute(
    bannerTitle: String,
    onCreateProject: () -> Unit,
    modifier: Modifier = Modifier,
    progressFieldState: FocusQuestProgressFieldState = rememberFocusQuestProgressFieldState(),
) {
    ProjectScreen(
        bannerTitle = bannerTitle,
        onCreateProject = onCreateProject,
        modifier = modifier,
        progressFieldState = progressFieldState,
    )
}

@Composable
fun ProjectScreen(
    onCreateProject: () -> Unit,
    modifier: Modifier = Modifier,
    progressFieldState: FocusQuestProgressFieldState = rememberFocusQuestProgressFieldState(),
    bannerTitle: String = stringResource(R.string.project_title),
) {
    FocusQuestEmptyProjectLayout(
        bannerTitle = bannerTitle,
        headline = stringResource(R.string.project_empty_headline),
        body = stringResource(R.string.project_empty_body),
        actionLabel = stringResource(R.string.project_create),
        onAction = onCreateProject,
        modifier = modifier,
        progressFieldState = progressFieldState,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFAF7F1, widthDp = 402, heightDp = 874)
@Composable
private fun ProjectEmptyPreview() {
    FocusQuestTheme {
        ProjectScreen(onCreateProject = {})
    }
}
