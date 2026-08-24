plugins {
    `kotlin-dsl`
}

group = "com.johnvazna.focusquest.buildlogic"

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.compose.compiler.gradle.plugin)
}
