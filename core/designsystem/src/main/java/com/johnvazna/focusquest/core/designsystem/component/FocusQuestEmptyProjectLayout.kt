package com.johnvazna.focusquest.core.designsystem.component

import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun FocusQuestEmptyProjectLayout(
    bannerTitle: String,
    headline: String,
    body: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    progressFieldState: FocusQuestProgressFieldState = rememberFocusQuestProgressFieldState(),
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val compactLayout = maxHeight < EmptyProjectDimensions.minExpandedHeight
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (compactLayout) Modifier.verticalScroll(rememberScrollState()) else Modifier,
                )
                .padding(horizontal = EmptyProjectDimensions.horizontalPadding)
                .padding(
                    top = EmptyProjectDimensions.topPadding,
                    bottom = EmptyProjectDimensions.bottomPadding,
                ),
        ) {
            AnimatedContent(
                targetState = bannerTitle,
                transitionSpec = {
                    (fadeIn(tween(BANNER_TRANSITION_DURATION_MS)) +
                        slideInVertically(
                            animationSpec = tween(BANNER_TRANSITION_DURATION_MS),
                            initialOffsetY = { height -> height / 3 },
                        )).togetherWith(
                        fadeOut(tween(BANNER_TRANSITION_DURATION_MS)) +
                            slideOutVertically(
                                animationSpec = tween(BANNER_TRANSITION_DURATION_MS),
                                targetOffsetY = { height -> -height / 4 },
                            ),
                    )
                },
                label = "emptyProjectBannerTitle",
            ) { title ->
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            Spacer(
                Modifier.height(
                    if (compactLayout) {
                        EmptyProjectDimensions.compactHeaderGap
                    } else {
                        EmptyProjectDimensions.headerGap
                    },
                ),
            )
            FocusQuestProgressField(
                progress = 0f,
                state = progressFieldState,
                modifier = Modifier
                    .widthIn(max = EmptyProjectDimensions.progressFieldSize)
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .align(Alignment.CenterHorizontally),
            )
            Spacer(
                if (compactLayout) {
                    Modifier.height(EmptyProjectDimensions.compactContentGap)
                } else {
                    Modifier.weight(1f)
                },
            )
            FocusQuestEmptyAction(
                headline = headline,
                body = body,
                actionLabel = actionLabel,
                onAction = onAction,
            )
        }
    }
}

@Composable
private fun FocusQuestProgressField(
    progress: Float,
    state: FocusQuestProgressFieldState,
    modifier: Modifier = Modifier,
) {
    val dots = remember(progress) { progressFieldDots(progress) }
    val ink = MaterialTheme.colorScheme.onBackground
    val animationScale = animatorDurationScale()
    val entrance = state.entrance

    LaunchedEffect(dots, animationScale) {
        if (animationScale == 0f) {
            entrance.snapTo(1f)
        } else {
            entrance.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = progressFieldEntranceDurationMillis(dots),
                    easing = LinearEasing,
                ),
            )
        }
    }

    val elapsedFraction by entrance.asState()
    Canvas(
        modifier = modifier
            .testTag(FOCUS_QUEST_PROGRESS_FIELD_TAG)
            .semantics { hideFromAccessibility() },
    ) {
        val elapsedMillis = elapsedFraction * progressFieldEntranceDurationMillis(dots)
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

/** Keeps the decorative field continuous while the empty Dashboard and Project routes alternate. */
@Stable
class FocusQuestProgressFieldState internal constructor() {
    internal val entrance = Animatable(0f)
}

@Composable
fun rememberFocusQuestProgressFieldState(): FocusQuestProgressFieldState =
    remember { FocusQuestProgressFieldState() }

private fun ProgressFieldDot.settleFraction(elapsedMillis: Float): Float {
    val startMillis = delaySeconds * 1000f
    if (elapsedMillis <= startMillis) return 0f
    val raw = (elapsedMillis - startMillis) / PROGRESS_FIELD_ENTRANCE_DURATION_MS
    return SettleEasing.transform(raw.coerceIn(0f, 1f))
}

private fun ProgressFieldDot.colorAt(settle: Float, ink: Color): Color {
    val appear = (settle / APPEAR_FRACTION).coerceAtMost(1f)
    if (lit) return ink.copy(alpha = appear)
    val recedeStart = if (holdsInk) INK_HOLD_FRACTION else APPEAR_FRACTION
    val recede = ((settle - recedeStart) / (1f - recedeStart)).coerceIn(0f, 1f)
    return ink.copy(alpha = appear * (1f - recede * (1f - RESTING_ALPHA)))
}

@Composable
private fun animatorDurationScale(): Float {
    val resolver = LocalContext.current.contentResolver
    return remember(resolver) {
        Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
    }
}

const val FOCUS_QUEST_PROGRESS_FIELD_TAG = "focus_quest_progress_field"

private const val DEGREES_TO_RADIANS = (PI / 180.0).toFloat()
private val SettleEasing = CubicBezierEasing(0.25f, 0.7f, 0.2f, 1f)
private const val APPEAR_FRACTION = 0.38f
private const val INK_HOLD_FRACTION = 0.78f
private const val RESTING_ALPHA = 0.22f
private const val BANNER_TRANSITION_DURATION_MS = 280

private object EmptyProjectDimensions {
    val minExpandedHeight = 700.dp
    val horizontalPadding = 32.dp
    val topPadding = 4.dp
    val bottomPadding = 24.dp
    val headerGap = 104.dp
    val compactHeaderGap = 48.dp
    val compactContentGap = 40.dp
    val progressFieldSize = 260.dp
}
