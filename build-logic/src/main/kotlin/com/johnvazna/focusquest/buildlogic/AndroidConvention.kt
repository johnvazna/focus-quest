package com.johnvazna.focusquest.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion

internal fun ApplicationExtension.configureFocusQuestAndroid() {
    compileSdk = 37

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    configureLint()
}

internal fun LibraryExtension.configureFocusQuestAndroid() {
    compileSdk = 37

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    configureLint()
}

private fun ApplicationExtension.configureLint() {
    lint {
        applyFocusQuestLintRules()
    }
}

private fun LibraryExtension.configureLint() {
    lint {
        applyFocusQuestLintRules()
    }
}

private fun com.android.build.api.dsl.Lint.applyFocusQuestLintRules() {
    abortOnError = true
    checkAllWarnings = true
    warningsAsErrors = true
    disable += setOf(
        "AndroidGradlePluginVersion",
        "GradleDependency",
        "NewerVersionAvailable",
    )
    htmlReport = true
    sarifReport = true
    xmlReport = true
}
