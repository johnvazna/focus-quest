plugins {
    id("focusquest.android.library.compose")
}

android {
    namespace = "com.johnvazna.focusquest.core.designsystem"
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    testImplementation(libs.junit)

    debugImplementation(libs.androidx.compose.ui.tooling)
}
