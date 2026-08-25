package com.johnvazna.focusquest

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.johnvazna.focusquest.app.FocusQuestApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // FocusQuest renders a single light treatment, so the system bars keep dark icons
        // regardless of the system-wide theme.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        setContent {
            FocusQuestApp(
                focusSessionViewModelFactory = (application as FocusQuestApplication)
                    .container
                    .focusSessionViewModelFactory,
            )
        }
    }
}
