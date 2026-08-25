package com.johnvazna.focusquest.feature.focussession.impl.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.johnvazna.focusquest.core.designsystem.theme.FocusQuestTheme
import com.johnvazna.focusquest.feature.focussession.impl.R
import com.johnvazna.focusquest.feature.focussession.impl.domain.model.FocusSessionPolicy

@Composable
fun FocusSessionRoute(
    viewModel: FocusSessionViewModel,
    onEffect: (FocusSessionUiEffect) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.effects.collect(onEffect)
    }

    FocusSessionScreen(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusSessionScreen(
    state: FocusSessionUiState,
    onEvent: (FocusSessionUiEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text(stringResource(R.string.focus_session_title)) }) },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            when (state.status) {
                FocusSessionUiStatus.IDLE -> IdleContent(state, onEvent)
                FocusSessionUiStatus.STARTING -> StatusMessage(R.string.focus_session_starting)
                FocusSessionUiStatus.ACTIVE,
                FocusSessionUiStatus.PAUSED,
                -> RunningContent(state, onEvent)
                FocusSessionUiStatus.COMPLETED -> StatusMessage(R.string.focus_session_completed)
                FocusSessionUiStatus.CANCELLED -> StatusMessage(R.string.focus_session_cancelled)
            }
        }
    }
}

@Composable
private fun IdleContent(
    state: FocusSessionUiState,
    onEvent: (FocusSessionUiEvent) -> Unit,
) {
    Text(
        text = stringResource(R.string.focus_session_duration),
        style = MaterialTheme.typography.titleMedium,
    )
    Spacer(Modifier.height(16.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        durationOptions.forEach { minutes ->
            FilterChip(
                selected = state.selectedDurationMinutes == minutes,
                onClick = { onEvent(FocusSessionUiEvent.DurationChanged(minutes)) },
                label = { Text(stringResource(R.string.focus_session_minutes, minutes)) },
            )
        }
    }
    Spacer(Modifier.height(32.dp))
    Button(
        onClick = { onEvent(FocusSessionUiEvent.Start) },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(stringResource(R.string.focus_session_start))
    }
}

@Composable
private fun RunningContent(
    state: FocusSessionUiState,
    onEvent: (FocusSessionUiEvent) -> Unit,
) {
    Text(
        text = pluralStringResource(
            R.plurals.focus_session_remaining,
            state.remainingMinutes,
            state.remainingMinutes,
        ),
        style = MaterialTheme.typography.headlineMedium,
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = stringResource(
            R.string.focus_session_pause_count,
            state.pauseCount,
            FocusSessionPolicy.MAX_PAUSE_COUNT,
        ),
        style = MaterialTheme.typography.bodyMedium,
    )
    Spacer(Modifier.height(32.dp))
    Button(
        onClick = {
            onEvent(
                if (state.status == FocusSessionUiStatus.ACTIVE) {
                    FocusSessionUiEvent.Pause
                } else {
                    FocusSessionUiEvent.Resume
                },
            )
        },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            stringResource(
                if (state.status == FocusSessionUiStatus.ACTIVE) {
                    R.string.focus_session_pause
                } else {
                    R.string.focus_session_resume
                },
            ),
        )
    }
    Spacer(Modifier.height(8.dp))
    OutlinedButton(
        onClick = { onEvent(FocusSessionUiEvent.Cancel) },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(stringResource(R.string.focus_session_cancel))
    }
}

@Composable
private fun StatusMessage(messageResource: Int) {
    Text(
        text = stringResource(messageResource),
        style = MaterialTheme.typography.headlineMedium,
    )
}

private val durationOptions = listOf(15, 25, 45, 60)

@Preview(showBackground = true)
@Composable
private fun FocusSessionIdlePreview() {
    FocusQuestTheme {
        FocusSessionScreen(
            state = FocusSessionUiState(),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FocusSessionActivePreview() {
    FocusQuestTheme {
        FocusSessionScreen(
            state = FocusSessionUiState(
                remainingMinutes = 18,
                pauseCount = 1,
                status = FocusSessionUiStatus.ACTIVE,
            ),
            onEvent = {},
        )
    }
}
