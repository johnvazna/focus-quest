package com.johnvazna.focusquest.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.johnvazna.focusquest.feature.focussession.impl.presentation.FocusSessionRoute
import com.johnvazna.focusquest.feature.focussession.impl.presentation.FocusSessionUiEffect

@Composable
fun FocusQuestNavHost(
    focusSessionViewModelFactory: ViewModelProvider.Factory,
    onFocusSessionEffect: (FocusSessionUiEffect) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = FOCUS_SESSION_ROUTE,
        modifier = modifier,
    ) {
        composable(FOCUS_SESSION_ROUTE) {
            FocusSessionRoute(
                viewModel = viewModel(factory = focusSessionViewModelFactory),
                onEffect = onFocusSessionEffect,
            )
        }
    }
}

private const val FOCUS_SESSION_ROUTE = "focus-session"
