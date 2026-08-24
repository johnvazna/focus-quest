import com.android.build.api.dsl.ApplicationExtension
import com.johnvazna.focusquest.buildlogic.configureFocusQuestAndroid

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

extensions.configure<ApplicationExtension> {
    configureFocusQuestAndroid()

    buildFeatures {
        compose = true
    }
}
