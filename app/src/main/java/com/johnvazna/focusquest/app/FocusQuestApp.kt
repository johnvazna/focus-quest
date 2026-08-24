package com.johnvazna.focusquest.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModelProvider
import com.johnvazna.focusquest.R
import com.johnvazna.focusquest.app.navigation.FocusQuestNavHost
import com.johnvazna.focusquest.core.designsystem.theme.FocusQuestTheme
import com.johnvazna.focusquest.feature.focussession.impl.presentation.FocusSessionUiEffect
import kotlinx.coroutines.launch

@Composable
fun FocusQuestApp(
    focusSessionViewModelFactory: ViewModelProvider.Factory,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val alreadyActiveMessage = stringResource(R.string.focus_session_already_active)
    val invalidDurationMessage = stringResource(R.string.focus_session_invalid_duration)

    FocusQuestTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            FocusQuestNavHost(
                focusSessionViewModelFactory = focusSessionViewModelFactory,
                onFocusSessionEffect = { effect ->
                    val message = when (effect) {
                        FocusSessionUiEffect.SessionAlreadyActive -> alreadyActiveMessage
                        FocusSessionUiEffect.InvalidDuration -> invalidDurationMessage
                    }
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(message)
                    }
                },
                modifier = Modifier.fillMaxSize(),
            )
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}
