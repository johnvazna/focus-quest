package com.johnvazna.focusquest.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.johnvazna.focusquest.R
import com.johnvazna.focusquest.core.designsystem.theme.FocusQuestTheme

@Composable
fun FocusQuestApp() {
    FocusQuestTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = stringResource(R.string.app_name))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FocusQuestAppPreview() {
    FocusQuestApp()
}
