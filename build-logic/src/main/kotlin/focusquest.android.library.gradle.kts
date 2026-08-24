import com.android.build.api.dsl.LibraryExtension
import com.johnvazna.focusquest.buildlogic.configureFocusQuestAndroid

plugins {
    id("com.android.library")
}

extensions.configure<LibraryExtension> {
    configureFocusQuestAndroid()
}
