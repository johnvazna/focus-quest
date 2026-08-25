package com.johnvazna.focusquest.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Shared visual structure for feature-owned empty states with one primary action. */
@Composable
fun FocusQuestEmptyAction(
    headline: String,
    body: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = headline,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(EmptyActionDimensions.headlineBodyGap))
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
            modifier = Modifier.widthIn(max = EmptyActionDimensions.bodyMaxWidth),
        )
        Spacer(Modifier.height(EmptyActionDimensions.bodyButtonGap))
        Button(
            onClick = onAction,
            modifier = Modifier.height(EmptyActionDimensions.buttonHeight),
            shape = RoundedCornerShape(EmptyActionDimensions.buttonHeight / 2),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            contentPadding = PaddingValues(
                horizontal = EmptyActionDimensions.buttonHorizontalPadding,
            ),
        ) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

private object EmptyActionDimensions {
    val headlineBodyGap = 15.dp
    val bodyMaxWidth = 270.dp
    val bodyButtonGap = 36.dp
    val buttonHeight = 54.dp
    val buttonHorizontalPadding = 30.dp
}
