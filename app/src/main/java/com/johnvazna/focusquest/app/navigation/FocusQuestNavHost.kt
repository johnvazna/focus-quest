package com.johnvazna.focusquest.app.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.johnvazna.focusquest.R
import com.johnvazna.focusquest.core.designsystem.component.rememberFocusQuestProgressFieldState
import com.johnvazna.focusquest.feature.focussession.impl.presentation.FocusSessionRoute
import com.johnvazna.focusquest.feature.focussession.impl.presentation.FocusSessionUiEffect
import com.johnvazna.focusquest.feature.project.impl.presentation.ProjectRoute

@Composable
fun FocusQuestNavHost(
    focusSessionViewModelFactory: ViewModelProvider.Factory,
    snackbarHostState: SnackbarHostState,
    onFocusSessionEffect: (FocusSessionUiEffect) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val emptyProjectProgressFieldState = rememberFocusQuestProgressFieldState()
    var selectedEmptyRoute by rememberSaveable { mutableStateOf(DASHBOARD_ROUTE) }
    val selectedNavigationRoute = if (currentRoute == EMPTY_PROJECT_ROUTE) {
        selectedEmptyRoute
    } else {
        currentRoute
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            FocusQuestNavigationBar(
                selectedRoute = selectedNavigationRoute,
                onNavigate = { route ->
                    if (route in emptyProjectRoutes) {
                        selectedEmptyRoute = route
                        if (currentRoute != EMPTY_PROJECT_ROUTE) {
                            navController.navigate(EMPTY_PROJECT_ROUTE) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    } else {
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
            )
        },
    ) { contentPadding ->
        NavHost(
            navController = navController,
            startDestination = EMPTY_PROJECT_ROUTE,
            modifier = Modifier.padding(contentPadding),
        ) {
            composable(EMPTY_PROJECT_ROUTE) {
                ProjectRoute(
                    bannerTitle = stringResource(
                        when (selectedEmptyRoute) {
                            PROJECT_ROUTE -> R.string.navigation_project
                            TIMELINE_ROUTE -> R.string.navigation_timeline
                            else -> R.string.navigation_dashboard
                        },
                    ),
                    // TODO: Connect the project-creation flow when its product design exists.
                    onCreateProject = { selectedEmptyRoute = PROJECT_ROUTE },
                    progressFieldState = emptyProjectProgressFieldState,
                )
            }
            composable(FOCUS_SESSION_ROUTE) {
                FocusSessionRoute(
                    viewModel = viewModel(factory = focusSessionViewModelFactory),
                    onEffect = onFocusSessionEffect,
                )
            }
        }
    }
}

@Composable
private fun FocusQuestNavigationBar(
    selectedRoute: String?,
    onNavigate: (String) -> Unit,
) {
    Column {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 14.dp)
                .padding(top = 10.dp, bottom = 12.dp),
        ) {
            navigationItems.forEach { item ->
                FocusQuestNavigationItem(
                    item = item,
                    selected = selectedRoute == item.route,
                    onClick = { item.route?.let(onNavigate) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun FocusQuestNavigationItem(
    item: NavigationItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ink = MaterialTheme.colorScheme.onBackground
    val label = stringResource(item.labelResource)

    Column(
        modifier = modifier.selectable(
            selected = selected,
            // Destinations that do not exist yet are exposed as unavailable rather than as
            // interactive controls that silently do nothing.
            enabled = item.route != null,
            role = Role.Tab,
            onClick = onClick,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Column(modifier = Modifier.padding(vertical = 6.dp)) {
            when (item) {
                is NavigationItem.Marked -> Icon(
                    painter = painterResource(item.icon),
                    contentDescription = null,
                    tint = if (selected) ink else ink.copy(alpha = ICON_RESTING_ALPHA),
                    modifier = Modifier.size(20.dp),
                )
                is NavigationItem.Field -> DotFieldMark(
                    color = if (selected) ink else ink.copy(alpha = ICON_RESTING_ALPHA),
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
            color = if (selected) ink else ink.copy(alpha = LABEL_RESTING_ALPHA),
        )
    }
}

/**
 * The Dashboard tab carries the same dot field the dashboard itself draws, reduced to a mark: an
 * even grid of dots faded out towards the edge of a circle.
 */
@Composable
private fun DotFieldMark(
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.size(20.dp)) {
        val spacing = MARK_DOT_SPACING_DP.dp.toPx()
        val dotRadius = MARK_DOT_RADIUS_DP.dp.toPx()
        val center = Offset(size.width / 2f, size.height / 2f)
        val solidRadius = size.minDimension / 2f * MARK_SOLID_FRACTION
        val fadedRadius = size.minDimension / 2f * MARK_FADED_FRACTION

        var y = spacing / 2f
        while (y < size.height) {
            var x = spacing / 2f
            while (x < size.width) {
                val distance = (Offset(x, y) - center).getDistance()
                val visibility = when {
                    distance <= solidRadius -> 1f
                    distance >= fadedRadius -> 0f
                    else -> 1f - (distance - solidRadius) / (fadedRadius - solidRadius)
                }
                if (visibility > 0f) {
                    drawCircle(
                        color = color.copy(alpha = color.alpha * visibility),
                        radius = dotRadius,
                        center = Offset(x, y),
                    )
                }
                x += spacing
            }
            y += spacing
        }
    }
}

private sealed interface NavigationItem {
    val labelResource: Int
    val route: String?

    data class Marked(
        @param:StringRes override val labelResource: Int,
        @param:DrawableRes val icon: Int,
        override val route: String?,
    ) : NavigationItem

    data class Field(
        @param:StringRes override val labelResource: Int,
        override val route: String?,
    ) : NavigationItem
}

private val navigationItems = listOf(
    NavigationItem.Marked(R.string.navigation_project, R.drawable.ic_nav_project, PROJECT_ROUTE),
    NavigationItem.Field(R.string.navigation_dashboard, DASHBOARD_ROUTE),
    NavigationItem.Marked(R.string.navigation_timeline, R.drawable.ic_nav_timeline, TIMELINE_ROUTE),
    NavigationItem.Marked(R.string.navigation_settings, R.drawable.ic_nav_settings, null),
)

private const val DASHBOARD_ROUTE = "dashboard"
private const val PROJECT_ROUTE = "project"
private const val TIMELINE_ROUTE = "timeline"
private const val EMPTY_PROJECT_ROUTE = "empty-project"
private const val FOCUS_SESSION_ROUTE = "focus-session"

private const val ICON_RESTING_ALPHA = 0.22f
private const val LABEL_RESTING_ALPHA = 0.28f

private const val MARK_DOT_SPACING_DP = 4.2f
private const val MARK_DOT_RADIUS_DP = 1.05f
private const val MARK_SOLID_FRACTION = 0.55f
private const val MARK_FADED_FRACTION = 0.78f

private val emptyProjectRoutes = setOf(DASHBOARD_ROUTE, PROJECT_ROUTE, TIMELINE_ROUTE)
