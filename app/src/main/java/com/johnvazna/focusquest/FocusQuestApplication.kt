package com.johnvazna.focusquest

import android.app.Application
import com.johnvazna.focusquest.app.di.AppContainer

class FocusQuestApplication : Application() {
    val container: AppContainer by lazy(LazyThreadSafetyMode.NONE) {
        AppContainer(applicationContext)
    }
}
