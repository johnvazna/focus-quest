package com.johnvazna.focusquest.feature.dashboard.impl.presentation

import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.johnvazna.focusquest.core.designsystem.theme.FocusQuestTheme
import com.johnvazna.focusquest.feature.dashboard.impl.R
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DashboardRoute(
    onCreateProject: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DashboardScreen(
        onCreateProject = onCreateProject,
        modifier = modifier,
    )
}

/**
 * Dashboard before a project exists.
 *
 * There is no metric yet, so the screen shows no percentage, no empty cards and no illustration.
 * It states what the dashboard will measure and offers the one action that creates something to
 * measure.
 */
@Composable
fun DashboardScreen(
    onCreateProject: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val compactLayout = maxHeight < DashboardDimensions.minExpandedHeight

        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (compactLayout) {
                        Modifier.verticalScroll(rememberScrollState())
                    } else {
                        Modifier
                    },
                )
                .padding(horizontal = DashboardDimensions.horizontalPadding)
                .padding(
                    top = DashboardDimensions.topPadding,
                    bottom = DashboardDimensions.bottomPadding,
                ),
        ) {
            Text(
                text = stringResource(R.string.dashboard_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(
                Modifier.height(
                    if (compactLayout) {
                        DashboardDimensions.compactHeaderGap
                    } else {
                        DashboardDimensions.headerGap
                    },
                ),
            )
            ProjectProgressField(
                progress = 0f,
                modifier = Modifier
                    .widthIn(max = DashboardDimensions.progressFieldSize)
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .align(Alignment.CenterHorizontally),
            )
            Spacer(
                if (compactLayout) {
                    Modifier.height(DashboardDimensions.compactContentGap)
                } else {
                    Modifier.weight(1f)
                },
            )
            DashboardEmptyAction(onCreateProject = onCreateProject)
        }
    }
}

@Composable
private fun DashboardEmptyAction(onCreateProject: () -> Unit) {
    Text(
        text = stringResource(R.string.dashboard_empty_headline),
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onBackground,
    )
    Spacer(Modifier.height(DashboardDimensions.headlineBodyGap))
    Text(
        text = stringResource(R.string.dashboard_empty_body),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
        modifier = Modifier.widthIn(max = DashboardDimensions.bodyMaxWidth),
    )
    Spacer(Modifier.height(DashboardDimensions.bodyButtonGap))
    Button(
        onClick = onCreateProject,
        modifier = Modifier.height(DashboardDimensions.buttonHeight),
        shape = RoundedCornerShape(DashboardDimensions.buttonHeight / 2),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        contentPadding = PaddingValues(horizontal = DashboardDimensions.buttonHorizontalPadding),
    ) {
        Text(
            text = stringResource(R.string.dashboard_create_project),
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

/**
 * The field carries no information while the project does not exist: nothing is painted in, so
 * every dot is identical. It is decorative here and is hidden from accessibility services rather
 * than announced as an empty progress indicator.
 */
@Composable
private fun ProjectProgressField(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    val dots = remember(progress) { progressFieldDots(progress) }
    val ink = MaterialTheme.colorScheme.onBackground
    val animationScale = animatorDurationScale()
    val entrance = remember(dots) { Animatable(if (animationScale == 0f) 1f else 0f) }

    LaunchedEffect(dots, animationScale) {
        if (animationScale == 0f) {
            entrance.snapTo(1f)
            return@LaunchedEffect
        }
        entrance.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = progressFieldEntranceDurationMillis(dots),
                easing = LinearEasing,
            ),
        )
    }

    val elapsedFraction by entrance.asState()

    Canvas(
        modifier = modifier
            .testTag(PROJECT_PROGRESS_FIELD_TAG)
            .semantics { hideFromAccessibility() },
    ) {
        val timelineMillis = progressFieldEntranceDurationMillis(dots)
        val elapsedMillis = elapsedFraction * timelineMillis
        val center = Offset(size.width / 2f, size.height / 2f)
        val restingRadius = PROGRESS_FIELD_DOT_DIAMETER_DP.dp.toPx() / 2f
        val litRadius = PROGRESS_FIELD_LIT_DOT_DIAMETER_DP.dp.toPx() / 2f
        val ringSpacing = (size.minDimension / 2f - litRadius) / PROGRESS_FIELD_RINGS

        dots.forEach { dot ->
            val settle = dot.settleFraction(elapsedMillis)
            if (settle <= 0f) return@forEach

            val angleRadians = dot.angleDegrees * DEGREES_TO_RADIANS
            val distance = dot.ring * ringSpacing
            drawCircle(
                color = dot.colorAt(settle, ink),
                radius = if (dot.lit) litRadius else restingRadius,
                center = Offset(
                    x = center.x + distance * sin(angleRadians),
                    y = center.y - distance * cos(angleRadians),
                ),
            )
        }
    }
}

/** How far this dot has progressed through its own entrance, ignoring its stagger delay. */
private fun ProgressFieldDot.settleFraction(elapsedMillis: Float): Float {
    val startMillis = delaySeconds * 1000f
    if (elapsedMillis <= startMillis) return 0f
    val raw = (elapsedMillis - startMillis) / PROGRESS_FIELD_ENTRANCE_DURATION_MS
    return SettleEasing.transform(raw.coerceIn(0f, 1f))
}

/**
 * A dot arrives at full ink and then recedes to its resting weight. A dot that [holdsInk] keeps
 * the ink longer, which is what gives the field its uneven, hand-set texture.
 */
private fun ProgressFieldDot.colorAt(settle: Float, ink: Color): Color {
    val appear = (settle / APPEAR_FRACTION).coerceAtMost(1f)
    if (lit) return ink.copy(alpha = appear)

    val recedeStart = if (holdsInk) INK_HOLD_FRACTION else APPEAR_FRACTION
    val recede = ((settle - recedeStart) / (1f - recedeStart)).coerceIn(0f, 1f)
    val weight = 1f - recede * (1f - RESTING_ALPHA)
    return ink.copy(alpha = appear * weight)
}

@Composable
private fun animatorDurationScale(): Float {
    val resolver = LocalContext.current.contentResolver
    return remember(resolver) {
        Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
    }
}

private const val DEGREES_TO_RADIANS = (PI / 180.0).toFloat()

private val SettleEasing = CubicBezierEasing(0.25f, 0.7f, 0.2f, 1f)
private const val APPEAR_FRACTION = 0.38f
private const val INK_HOLD_FRACTION = 0.78f
private const val RESTING_ALPHA = 0.22f

private object DashboardDimensions {
    val minExpandedHeight = 700.dp
    val horizontalPadding = 32.dp
    val topPadding = 4.dp
    val bottomPadding = 24.dp
    val headerGap = 104.dp
    val compactHeaderGap = 48.dp
    val compactContentGap = 40.dp
    val progressFieldSize = 260.dp
    val headlineBodyGap = 15.dp
    val bodyMaxWidth = 270.dp
    val bodyButtonGap = 36.dp
    val buttonHeight = 54.dp
    val buttonHorizontalPadding = 30.dp
}

const val PROJECT_PROGRESS_FIELD_TAG = "project_progress_field"

@Preview(showBackground = true, backgroundColor = 0xFFFAF7F1, widthDp = 402, heightDp = 874)
@Composable
private fun DashboardEmptyPreview() {
    FocusQuestTheme {
        DashboardScreen(onCreateProject = {})
    }
}
