plugins {
    id("focusquest.android.library")
}

android {
    namespace = "com.johnvazna.focusquest.feature.focussession.impl"
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
