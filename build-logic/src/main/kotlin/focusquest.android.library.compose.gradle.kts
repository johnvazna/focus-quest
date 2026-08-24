import com.android.build.api.dsl.LibraryExtension

plugins {
    id("focusquest.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
}

extensions.configure<LibraryExtension> {
    buildFeatures {
        compose = true
    }
}
